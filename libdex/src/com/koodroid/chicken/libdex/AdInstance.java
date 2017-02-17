package com.koodroid.chicken.libdex;

import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.InterstitialAd;
import com.baidu.mobads.InterstitialAdListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

public class AdInstance {
    
    public static View getBaiduBannerView(Activity activity) {
        Log.e("daniel","with mob");
    	MobclickAgent.setDebugMode(false);
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
    
    public static void showBaiduInterstitialAd(final Activity activity,final boolean delay) {
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
                clearRunCount();
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
    
    private static void clearRunCount() {
//        mPrefs.edit().putInt("lastCount", 0).commit();
//        mOnResumeTime = System.currentTimeMillis();
        Log.d("daniel","clearRunCount  ");
    }

}
