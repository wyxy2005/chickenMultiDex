package com.koodroid.chicken;

import static com.koodroid.chicken.MainActivity.BAIDU_AD_TYPE;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;


/**
 * Created by dingchao on 17-1-7.
 */

public class RSplashActivity extends Activity {

    private Context mContext;

    private SharedPreferences mPrefs;
    public static final String LAUNCH_COUNT = "launch_count";
    public static final String LAUNCH_THRESHOLD = "launch_threshold";

    public static final int LAUNCH_THRESHOLD_COUNT = 1;

    private int mLaunchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mPrefs = mContext.getSharedPreferences(
                MainActivity.class.getName(), Context.MODE_PRIVATE);

        mLaunchCount = mPrefs.getInt(LAUNCH_COUNT, 0);
        mLaunchCount = mLaunchCount + 1;
        mPrefs.edit().putInt(LAUNCH_COUNT,mLaunchCount).commit();

        int addType = mPrefs.getInt(MainActivity.JsonName, MainActivity.DEFAULT_AD_TYPE);

        if (!shouldShowSplashAd() || addType != BAIDU_AD_TYPE) {
            jump();
            return;
        }

        setContentView(R.layout.splash);
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);

        adsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpWhenCanClick();
            }
        });
        
        addBaiduSplash(adsParent);
    }
    
    private void addBaiduSplash(RelativeLayout v) {
    	boolean exception = false;
    	try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.libdex.AdInstance");

            final Class[] argsClass = new Class[2];
            argsClass[0] = Activity.class;
            argsClass[1] = RelativeLayout.class;
            final Method method = AdInstance
                    .getMethod("addBaiduSplashAd", argsClass);
            final Object value = method.invoke(null,this, v);
        } catch (final Exception e) {
            e.printStackTrace();
            exception = true;
        }
		if (exception) {
			this.getWindow().getDecorView().postDelayed(new Runnable() {
				@Override
				public void run() {
					jump();
				}
			}, 4000);
		}
    }

    boolean shouldShowSplashAd() {
        int launch_threshold = mPrefs.getInt(LAUNCH_THRESHOLD, LAUNCH_THRESHOLD_COUNT);
        boolean back = mPrefs.getBoolean("BACK_PRESSED",false);
        if ((mLaunchCount % launch_threshold) == 0 && !back) {
            return true;
        } else {
            return false;
        }
    }

    private boolean mBackPressed = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mBackPressed = true;
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加canJumpImmediately判断。 另外，点击开屏还需要在onResume中调用jumpWhenCanClick接口。
     */
    public boolean canJumpImmediately = false;

    public void jumpWhenCanClick() {
        Log.d("daniel", "this.hasWindowFocus():" + this.hasWindowFocus());
        if (canJumpImmediately) {
            jump();
        } else {
            canJumpImmediately = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    public void jump() {
        Log.i("daniel", "jump");
        this.startActivity(new Intent(RSplashActivity.this, MainActivity.class));
        mPrefs.edit().putBoolean("BACK_PRESSED",mBackPressed).commit();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }

    @Override
    protected  void onStop() {
        super.onStop();
        mPrefs.edit().putBoolean("BACK_PRESSED",mBackPressed).commit();
    }
}
