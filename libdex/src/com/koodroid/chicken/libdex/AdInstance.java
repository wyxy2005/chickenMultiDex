package com.koodroid.chicken.libdex;

import java.lang.reflect.Method;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;

import com.koodroid.chicken.*;

public class AdInstance {
    
    public static View getBaiduBannerView(Activity activity) {
        Log.e("daniel","test Version new");
    	String adPlaceId = "2911817";
    	AdView adView = new AdView(activity, adPlaceId);
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
            }

            public void onAdShow(JSONObject info) {
            }
            
            public void onAdReady(AdView adView) {
            }

            public void onAdFailed(String reason) {
                Log.d("chicken","baidu loadFailed:");
            }

            public void onAdClick(JSONObject info) {
            }

            @Override
            public void onAdClose(JSONObject arg0) {
            }
        });
        return adView;
    }
    
    public static void showBaiduInterstitialAd(final Activity activity) {
        Log.d("daniel","showBaiduInterstitialAd");

        String adPlaceId = "2911919"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        final InterstitialAd interAd = new InterstitialAd(activity, adPlaceId);
        interAd.setListener(new InterstitialAdListener() {

            @Override
            public void onAdClick(InterstitialAd arg0) {
                Log.i("daniel", "intialad onAdClick");
                interAd.destroy();
            }

            @Override
            public void onAdDismissed() {
                Log.i("daniel", "intialad onAdDismissed");
                interAd.loadAd();
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("daniel", "intialad onAdFailed");
            }

            @Override
            public void onAdPresent() {
                Log.i("daniel", "intialad onAdPresent");
                clearRunCount(activity);
            }

            @Override
            public void onAdReady() {
                Log.i("daniel", "intialad onAdReady");
            }

        });

        interAd.loadAd();
        activity.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (interAd.isAdReady()/* && !mPaused*/) {
                    interAd.showAd(activity);
                }
            }
        },5000);
    }
    
    public static void addBaiduSplashAd(final Activity activity,RelativeLayout adsParent) {

        // the observer of AD
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                Log.i("daniel", "splash onAdDismissed");
                splashJumpBack(activity);; // 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                Log.i("daniel", "splash onAdFailed");
                activity.getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	splashJumpBack(activity);
                    }
                },4000);
            }

            @Override
            public void onAdPresent() {
                Log.i("daniel", "splash onAdPresent");
                //MobclickAgent.onEvent(mContext, "splashPresent");
            }

            @Override
            public void onAdClick() {
                Log.i("daniel", "splash onAdClick");
                //MobclickAgent.onEvent(mContext, "splashClick");
                splashJumpBack(activity);
                // 设置开屏可接受点击时，该回调可用
            }
        };
        String adPlaceId = "2911823"; // 重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        new SplashAd(activity, adsParent, listener, adPlaceId, true);
    }
    
    private static void splashJumpBack(Activity activity) {
    	try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.RSplashActivity");
            final Method method = AdInstance
                    .getMethod("jumpWhenCanClick");
            final Object value = method.invoke(activity);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void clearRunCount(Activity activity) {
    	try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.MainActivity");
            final Method method = AdInstance
                    .getMethod("clearRunCount");
            final Object value = method.invoke(activity);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        Log.d("daniel","clearRunCount  in libdex");
    }

}
