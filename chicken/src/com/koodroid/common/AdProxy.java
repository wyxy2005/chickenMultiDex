package com.koodroid.common;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Method;

/**
 * Created by dingchao on 17-2-24.
 */

public class AdProxy {

    private static AdProxy sInstance = null;
    private Activity mActivity = null;
    public static AdProxy getInstance(Activity activity) {
        if (sInstance == null)
            sInstance = new AdProxy();

        sInstance.setActivity(activity);
        return sInstance;
    }

    private void setActivity(Activity activity) {
        mActivity = activity;
    }
    private void showInterstitialAd() {
        try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.libdex.AdInstance");

            final Class[] argsClass = new Class[1];
            argsClass[0] = Activity.class;
            final Method method = AdInstance
                    .getMethod("showBaiduInterstitialAd", argsClass);
            final Object value = method.invoke(null,this);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private View getBaiduBannerView() {
        View ret = null;
        try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.libdex.AdInstance");

            final Class[] argsClass = new Class[1];
            argsClass[0] = Activity.class;
            final Method method = AdInstance
                    .getMethod("getBaiduBannerView", argsClass);
            final Object value = method.invoke(null,this);
            ret = (View) value;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
