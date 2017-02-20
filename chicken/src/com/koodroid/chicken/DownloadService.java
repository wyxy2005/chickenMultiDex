package com.koodroid.chicken;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadService extends Service {
    //private DownloadManager dm;
    private long enqueue;
    private BroadcastReceiver receiver;

    private static final boolean DEBUG = false;
    private static final String testMarketUrl = "http://www.koodroid.com/download/chicken_check_test";
    private static final String UpdateUrl = "http://www.koodroid.com/download/chicken_check";

    private String mUpdateUrl;
    private String mDownloadUrl;
    private boolean mAlreadyCheckedForUpdates;
    private boolean mUpdateAvailable;
    private String mLatestVersion;
    
    private int mAdType = MainActivity.DEFAULT_AD_TYPE;
    private int mShowExtraAd = 5;
    private int mNoActionDelay = 30 * 1000;
    private int mLaunchThreshold = RSplashActivity.LAUNCH_THRESHOLD_COUNT;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getApplicationChannel() {
        ApplicationInfo info;
        try {
            info = this.getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);

            String msg = info.metaData.getString("UMENG_CHANNEL");
            String str = Build.MANUFACTURER;
            if (isOppo(str))
                return "Oppo";
            return msg;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isOppo(String man) {
        if (man == null)
            return false;
        String str = man.toLowerCase();
        if (str.startsWith("oppo") || str.startsWith("a")|| str.startsWith("r"))
            return true;
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent iintent) {
//                if ("com.koodroid.chicken".equals(iintent.getPackage())) {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.setDataAndType(Uri.fromFile(new File(Environment
//                            .getExternalStorageDirectory()
//                            + "/download/chicken.apk")),
//                            "application/vnd.android.package-archive");
//                    startActivity(intent);
//                    stopSelf();
//                }
//            }
//        };

//      registerReceiver(receiver, new IntentFilter(
//              DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        
        if (DEBUG) {
            mUpdateUrl = testMarketUrl;
        } else {
            String channel = getApplicationChannel();
            if (channel == null || channel.equalsIgnoreCase("Umeng")) {
                mUpdateUrl = UpdateUrl;
            } else {
                mUpdateUrl = UpdateUrl + "_" + channel;
            }
        }

        // startDownload();
        checkForUpdateOnBackgroundThread(DownloadService.this);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

//    private void startDownload() {
//        try {
//            dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//            DownloadManager.Request request = new DownloadManager.Request(
//                    Uri.parse(mDownloadUrl));
//            request.setMimeType("application/vnd.android.package-archive");
//            request.setDestinationInExternalPublicDir(
//                    Environment.DIRECTORY_DOWNLOADS, "chicken.apk");
//            enqueue = dm.enqueue(request);
//        } catch (Exception e) {
//
//        }
//    }

    public void checkForUpdateOnBackgroundThread(final DownloadService service) {
        if (mAlreadyCheckedForUpdates) {
            return;
        }
        mAlreadyCheckedForUpdates = true;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = service.getSharedPreferences(
                        MainActivity.class.getName(), Context.MODE_PRIVATE);
                // Random random = new Random();
                // long interval = random.nextInt(7) + 3; // [3, 10)
                long timestamp = prefs.getLong("LastCheckTimestamp", 0);
                long now = System.currentTimeMillis();
                if ((now - timestamp < 24 * 60 * 60 * 1000)) {
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
                        InputStreamReader reader = new InputStreamReader(
                                urlConnection.getInputStream());
                        in = new BufferedReader(reader);
                        StringBuilder response = new StringBuilder();
                        for (String line = in.readLine(); line != null; line = in
                                .readLine()) {
                            response.append(line);
                        }

                        try {
                            JSONObject jsonObj = new JSONObject(
                                    response.toString());
                            mAdType = jsonObj.getInt(MainActivity.JsonName);
                            mShowExtraAd = jsonObj.getInt(MainActivity.ShowExtraAd);
                            mNoActionDelay = jsonObj.getInt(MainActivity.NO_ACTION_DELAY);
                            mLaunchThreshold = jsonObj.getInt(RSplashActivity.LAUNCH_THRESHOLD);
                            mLatestVersion = jsonObj.getString("version_code");
                            mDownloadUrl = jsonObj.getString("download_url");
                        } catch (JSONException e) {
                            System.out.println("Json parse error");
                            e.printStackTrace();
                        }
                        
                        prefs.edit().putInt(MainActivity.JsonName, mAdType).commit();
                        prefs.edit().putInt(MainActivity.ShowExtraAd, mShowExtraAd).commit();
                        prefs.edit().putInt(MainActivity.NO_ACTION_DELAY, mNoActionDelay).commit();
                        prefs.edit().putInt(RSplashActivity.LAUNCH_THRESHOLD,mLaunchThreshold).commit();

                        Log.d("chicken","getSwitch:" + mAdType);
                        mUpdateAvailable = Integer.valueOf(mLatestVersion) > Integer
                                .valueOf(getPackageVersionCode(service));
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
                //if (mUpdateAvailable) {
                //    startDownload();
                //}
                mAlreadyCheckedForUpdates = false;
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
}