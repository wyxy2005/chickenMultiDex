// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.koodroid.chicken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;

/**
 * Java side version of chrome/common/url_constants.cc
 */
public class KooDroidHelper {
    private static final boolean DEBUG = true;
    private static KooDroidHelper sInstance;
    private static Object sGetInstanceLock = new Object();
    private static final String testMarketUrl = "http://www.koodroid.com/download/chicken_check";
    private static final String UpdateUrl = "http://www.koodroid.com/download/chicken_check";
    private static final String DownloadUrlFormat = "http://www.koodroid.com/downloads/chicken_%d.apk";
    private static final String HRRUrl = "http://www.crsafari.com/params/hrr";
    private static final String OfficialSite = "http://www.crsafari.com";

    private String mUpdateUrl;
    private String mDownloadUrl;
    private boolean mAlreadyCheckedForUpdates;
    private boolean mUpdateAvailable;
    private String mLatestVersion;

    private boolean mAlreadCheckedForHostResolverRulesEnabled;
    private boolean mHostResolverRulesEnabled;

    public static KooDroidHelper getInstance() {
        synchronized (KooDroidHelper.sGetInstanceLock) {
            if (sInstance == null) {
                sInstance = new KooDroidHelper();
                
                if (DEBUG) {
                    sInstance.mUpdateUrl = testMarketUrl;
                } else {
                    sInstance.mUpdateUrl = UpdateUrl;
                }
            }
            return sInstance;
        }
    }

    public void checkForUpdateOnBackgroundThread(final MainActivity activity) {
        if (mAlreadyCheckedForUpdates) {
            return;
        }
        mAlreadyCheckedForUpdates = true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = activity.getSharedPreferences(KooDroidHelper.class.getName(), Context.MODE_PRIVATE);
                Random random = new Random();
                long interval = random.nextInt(7) + 3; // [3, 10)
                long timestamp = prefs.getLong("LastCheckTimestamp", 0);
                long now = System.currentTimeMillis();
                if ((now - timestamp < interval * 24 * 60 * 60 * 1000) && !DEBUG) {
                    return null;
                }
                prefs.edit().putLong("LastCheckTimestamp", now).commit();
                HttpURLConnection urlConnection = null;
                BufferedReader in = null;
                try {
                    
                    
                    URL url = new URL(mUpdateUrl);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(60000);
                    urlConnection.setReadTimeout(60000);
                    urlConnection.setUseCaches(false);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                        in = new BufferedReader(reader);
                        StringBuilder response = new StringBuilder();
                        for (String line = in.readLine(); line != null; line = in.readLine()) {
                            response.append(line);
                        }
                        
                        try {   
                            JSONObject jsonObj = new JSONObject(response.toString());   
                            mLatestVersion = jsonObj.getString("version_code");   
                            mDownloadUrl = jsonObj.getString("download_url");  
                        } catch (JSONException e) {
                            System.out.println("Json parse error");   
                            e.printStackTrace();
                        }   
//                        
//                        JSONTokener jsonParser = new JSONTokener(response.toString());    
//                        // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。    
//                        // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）    
//                        JSONObject person = (JSONObject) jsonParser.nextValue();    
//                        // 接下来的就是JSON对象的操作了    
//                        person.getJSONArray("phone");    
//                        person.getString("name");    
//                        person.getInt("age");    
//                        person.getJSONObject("address");    
//                        person.getBoolean("married");    
//                        
                        //mLatestVersion = response.toString();
                        int versionCode = Integer.valueOf(mLatestVersion);
                        mUpdateAvailable = Integer.valueOf(mLatestVersion) > Integer.valueOf(getPackageVersionCode(activity));
                    }
                } catch (Exception e) {
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                        }
                    }
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                //maybeShowUpdateDialog(activity);
            }
        }.execute();
    }
    
    public static String getPackageVersionCode(Context context) {
        String msg = "versionCode not available.";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            msg = "";
            if (pi.versionCode > 0) {
                msg = Integer.toString(pi.versionCode);
            }
        } catch (NameNotFoundException e) {
        }
        return msg;
    }


//
//    private void maybeShowUpdateDialog(final MainActivity activity) {
//        if (activity.isDestroyed()) return;
//        if (!mUpdateAvailable) return;
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogTheme)
//        .setMessage(R.string.cr_update_title)
//        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        })
//        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (activity.isActivityDestroyed()) return;
//                activity.getCurrentTabCreator().launchUrl(OfficialSite, TabLaunchType.FROM_CHROME_UI);
//            }
//        });
//        builder.create().show();
//    }
}
