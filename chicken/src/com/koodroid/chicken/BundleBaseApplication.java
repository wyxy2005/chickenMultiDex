package com.koodroid.chicken;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import ctrip.android.bundle.framework.BundleCore;
import ctrip.android.bundle.framework.BundleException;
import ctrip.android.bundle.hotpatch.HotPatchManager;

/**
 * Created by yb.wang on 15/10/28.
 */
public class BundleBaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        SharedPreferences mPrefs = this.getSharedPreferences(
                MainActivity.class.getName(), Context.MODE_PRIVATE);
        if (!isOppo())
        	MainActivity.DEFAULT_AD_TYPE = MainActivity.BAIDU_AD_TYPE;
        int addType = mPrefs.getInt(MainActivity.JsonName, MainActivity.DEFAULT_AD_TYPE);
        if (addType == MainActivity.BAIDU_AD_TYPE)
        	installBundle();
    }
    private static Application mApplication;
    private static boolean mInstalled = false;
    public static void installBundle() {
    	if (mInstalled)
    		return;
    	mInstalled = true;
    	SharedPreferences sharedPreferences;
        boolean isDexInstalled = true;
        final String bundleKey;
        try {
            BundleCore.getInstance().init(mApplication);
            BundleCore.getInstance().ConfigLogger(true, 1);
            Properties properties = new Properties();
            properties.put("ctrip.android.sample.welcome", "ctrip.android.sample.WelcomeActivity"); // launch page
            sharedPreferences = mApplication.getSharedPreferences("bundlecore_configs", 0);
            String lastBundleKey = sharedPreferences.getString("last_bundle_key", "");
            Log.e("daniel","lastBundleKey:" + lastBundleKey);
            bundleKey = buildBundleKey();
            if (!TextUtils.equals(bundleKey, lastBundleKey)) {
                properties.put("com.koodroid.bundle.init", "true");
                isDexInstalled = false;
                HotPatchManager.getInstance().purge();
            }
            BundleCore.getInstance().startup(properties);
            if (isDexInstalled) {
                HotPatchManager.getInstance().run();
                BundleCore.getInstance().run();
            } else {
            	Log.e("daniel","install budle:");
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
                        try {
                            ZipFile zipFile = new ZipFile(mApplication.getApplicationInfo().sourceDir);
                            List bundleFiles = getBundleEntryNames(zipFile, BundleCore.LIB_PATH, ".so");
                            if (bundleFiles != null && bundleFiles.size() > 0) {
                            	Log.e("daniel","install budle1:");
                                processLibsBundles(zipFile, bundleFiles);
                                SharedPreferences.Editor edit = mApplication.getSharedPreferences("bundlecore_configs", 0).edit();
                                edit.putString("last_bundle_key", bundleKey);
                                edit.commit();
                            } else {
                                Log.e("Error Bundle", "not found com.koodroid.com.koodroid.bundle in apk");
                            }
                            if (zipFile != null) {
                                try {
                                    zipFile.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            BundleCore.getInstance().run();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

//                }).start();
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static String buildBundleKey() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), PackageManager.GET_CONFIGURATIONS);
        return String.valueOf(packageInfo.versionCode) + "_" + packageInfo.versionName;
    }

    private static List<String> getBundleEntryNames(ZipFile zipFile, String str, String str2) {
        List<String> arrayList = new ArrayList();
        try {
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                String name = ((ZipEntry) entries.nextElement()).getName();
                if (name.startsWith(str) && name.endsWith(str2)) {
                    arrayList.add(name);
                }
            }
        } catch (Throwable e) {
            Log.e("getBundleEntryNames", "Exception while get bundles in assets or lib", e);
        }
        return arrayList;
    }

    private static void processLibsBundles(ZipFile zipFile, List<String> list) {

        for (String str : list) {
            processLibsBundle(zipFile, str);
        }
    }

    private static boolean processLibsBundle(ZipFile zipFile, String str) {

        String packageNameFromEntryName = getPackageNameFromEntryName(str);
        Log.e("daniel","install budle2:" + packageNameFromEntryName);
        //if (BundleCore.getInstance().getBundle(packageNameFromEntryName) == null) {
            try {
                BundleCore.getInstance().installBundle(packageNameFromEntryName, zipFile.getInputStream(zipFile.getEntry(str)));
                Log.e("Succeed install", "Succeed to install com.koodroid.com.koodroid.bundle " + packageNameFromEntryName);
                return true;
            } catch (BundleException ex) {
                Log.e("Fail install", "Could not install com.koodroid.com.koodroid.bundle.", ex);
            } catch (IOException iex) {
                Log.e("Fail install", "Could not install com.koodroid.com.koodroid.bundle.", iex);
            }
        //}
        return false;
    }

    private static String getPackageNameFromEntryName(String entryName) {
        return entryName.substring(entryName.indexOf(BundleCore.LIB_PATH) + BundleCore.LIB_PATH.length(), entryName.indexOf(".so")).replace("_", ".");
    }
    

    private boolean isOppo() {
    	boolean isOppo = false;
    	ApplicationInfo info;
        try {
            info = this.getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);

            String msg = info.metaData.getString("UMENG_CHANNEL");
            String str = Build.MANUFACTURER;
            
            if (str == null)
                return false;
            String lowcase = str.toLowerCase();
            if (lowcase.startsWith("oppo") || lowcase.startsWith("a")|| lowcase.startsWith("r"))
                return true;
            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
