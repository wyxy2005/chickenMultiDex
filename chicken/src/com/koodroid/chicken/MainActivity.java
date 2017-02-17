package com.koodroid.chicken;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    public static final int BAIDU_AD_TYPE = 1;
    public static final int YOUDAO_AD_TYPE = 2;
    public static final int ADMOB_AD_TYPE = 3;


    public static final int DEFAULT_AD_TYPE = BAIDU_AD_TYPE;
    
    public static final String JsonName = "ad_type";
    public static final String ShowExtraAd = "show_extra_ad";
    public static final String NO_ACTION_DELAY = "no_action_delay";
    private Context mContext = null;
    private boolean mPaused;

    private final String mPageName = "Main";
    private LayoutInflater mLayoutInflater;

    public static final String TOTAL_RECORD = "total_record";
    public static final String MINITER_RECORD = "MINITER_record";

    ViewGroup mContentMainView;
    
    private RelativeLayout adContainer = null;

    private RelativeLayout mBaiduInterAd = null;

    private boolean isPlayChannel = false;
    
    private int mLastClickType = 0;
    private int mBaiduFailed = 0;

    private ImageView mPlayBtn;

    private boolean mStarted = false;

    private SharedPreferences mPrefs;

    private TextView mTvTotalScole;
    private TextView mTvTotalRecord;
    private TextView mTvMinScore;
    private TextView mTvMinRecord;

    private android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                handleRecord();
            } else{
                delayShowExtraAd();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        boolean isTablet = mContext.getResources().getBoolean(R.bool.isTablet);
        if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }



        mPrefs = mContext.getSharedPreferences(
                MainActivity.class.getName(), Context.MODE_PRIVATE);

        mTotalRecord = mPrefs.getInt(TOTAL_RECORD, 0);
        mMiniteRecord = mPrefs.getInt(MINITER_RECORD, 0);

        getApplicationChannel();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        setContentView(R.layout.main);

        //AppActivity.setActionBarColorTheme(ActionBarColorTheme.ACTION_BAR_WHITE_THEME);

        mContentMainView = (ViewGroup) this.findViewById(R.id.contents_main);

        ChickenMainView contentView = new ChickenMainView(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentMainView.addView(contentView, params);

        contentView.setActivity(this);

        mTvMinScore = (TextView) findViewById(R.id.score1);
        mTvTotalScole = (TextView) findViewById(R.id.score2);
        mTvMinRecord = (TextView) findViewById(R.id.score3);
        mTvTotalRecord = (TextView) findViewById(R.id.score4);

        mTvTotalRecord.setText(String.valueOf(mTotalRecord));
        mTvMinRecord.setText(String.valueOf(mMiniteRecord));

        
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(mContext, EScenarioType.E_UM_NORMAL);

        startService(new Intent(mContext, DownloadService.class));
        
        SoundPlayer.GetInstance(mContext);

        adContainer = (RelativeLayout) findViewById(R.id.banner_adcontainer);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    private void getApplicationChannel() {
        ApplicationInfo info;
        try {
            info = this.getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);

            String msg = info.metaData.getString("UMENG_CHANNEL");
            if (msg.equalsIgnoreCase("play"))
                isPlayChannel = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAdSwitch() {

    	View adView = getBaiduBannerView();
    	if (adView != null) {
    		Log.d("chicken","loadSwitch: not null");
    		adContainer.removeAllViews();
    		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            adContainer.addView(adView, rllp);
    	}
    	
        Log.d("chicken","loadSwitch:");

    }

    private void showExtraAdSwitch() {

         showBaiduInterstitialAd(false);
    }

    public void delayShowExtraAd() {
        int addType = mPrefs.getInt(JsonName, DEFAULT_AD_TYPE);
        if (addType == BAIDU_AD_TYPE)
            showBaiduInterstitialAd(true);
    }
    
    private void showBaiduInterstitialAd(boolean delay) {
    	try {
            final Class AdInstance = Class.forName("com.koodroid.chicken.libdex.AdInstance");

            final Class[] argsClass = new Class[2];
            argsClass[0] = Activity.class;
            argsClass[1] = Boolean.class;
            final Method method = AdInstance
                    .getMethod("showBaiduInterstitialAd", argsClass);
            final Object value = method.invoke(null,this, delay);
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

    public int getNoActionDelay() {
        return mPrefs.getInt(NO_ACTION_DELAY, 30 * 1000);
    }

    
    private long mOnResumeTime= 0;

    private void addRunCount(int count) {
        int lastCount = mPrefs.getInt("lastCount", 0);
        lastCount = lastCount+ count;
        mPrefs.edit().putInt("lastCount", lastCount).commit();
        Log.d("daniel","addRunCount  " + lastCount + "     " + count);
    }
    private boolean shouldShowExtraAd() {
        int lastCount = mPrefs.getInt("lastCount", 0);
        int configCount = mPrefs.getInt(ShowExtraAd, 6);
        Log.d("daniel","shouldShowExtraAd  " + lastCount);
        if (lastCount > configCount)
            return true;
        return false;
    }

    private void clearRunCount() {
        mPrefs.edit().putInt("lastCount", 0).commit();
        mOnResumeTime = System.currentTimeMillis();
        Log.d("daniel","clearRunCount  ");
    }

    public void setOnDownEgg() {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0,getNoActionDelay());

        mTotal ++;
        mTvTotalScole.setText(String.valueOf(mTotal));
        if (mTotal > mTotalRecord) {
            mTotalRecord = mTotal;
            mTvTotalRecord.setText(String.valueOf(mTotalRecord));
        }

        mCurrentCount ++;
        mTvMinScore.setText(String.valueOf(mCurrentCount + mRemainingCount));
        if (mCurrentCount + mRemainingCount > mMiniteRecord) {
            mMiniteRecord = mCurrentCount + mRemainingCount;
            mTvMinRecord.setText(String.valueOf(mMiniteRecord));
        }
    }

    private void handleRecord() {
        mValue[mCurrentIndex] = mCurrentCount;


        Log.d("daniel","handleRecord mCurrentIndex:" + mCurrentIndex + "   mCount:" + mCurrentCount + "  mRemaiCount:" + mRemainingCount);
        mCurrentIndex ++;
        if (mCurrentIndex >= sCount)
            mCurrentIndex = 0;

        mCurrentCount = 0;
        mRemainingCount = 0;
        int total = 0;
        for(int i:mValue)
            total += i;
        mRemainingCount = total - mValue[mCurrentIndex];
        mTvMinScore.setText(String.valueOf(total));
    }
    private Timer timer;

    private void startTimer() {
        timer = new Timer();
        timer.schedule(new java.util.TimerTask() {

            @Override
            public void run() {
                mHandler.sendEmptyMessage(1);
            }
        }, 60000/sCount, 60000/sCount);
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    };

    private static final int sCount = 30;
    private int[] mValue = new int[sCount];
    private int mCurrentIndex = 0;
    private int mCurrentCount = 0;
    private int mTotal = 0;
    private int mTotalRecord = 0;
    private int mMiniteRecord = 0;

    private int mRemainingCount = 0;

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
        if (timer != null) {
            timer.cancel();
        }
        startTimer();

        if (mTotalRecord == 0) {
            mTvMinRecord.setVisibility(View.INVISIBLE);
            mTvTotalRecord.setVisibility(View.INVISIBLE);
        } else {
            mTvMinRecord.setVisibility(View.VISIBLE);
            mTvTotalRecord.setVisibility(View.VISIBLE);
        }

        mOnResumeTime = System.currentTimeMillis();

        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageDelayed(0,getNoActionDelay());

        mBaiduFailed = 0;
        loadAdSwitch();

        if (shouldShowExtraAd()) {
            showExtraAdSwitch();
        }

        mPaused = false;
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
        SoundPlayer.GetInstance(mContext).setPaused(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;

        timer.cancel();

        mPrefs.edit().putInt(MINITER_RECORD, mMiniteRecord).commit();
        mPrefs.edit().putInt(TOTAL_RECORD, mTotalRecord).commit();

        mHandler.removeMessages(0);
        int addCount = 0;
        long now = System.currentTimeMillis();
        if (now - mOnResumeTime > 60 * 1000) {
            addCount = 2;
        } else if (now - mOnResumeTime > 20 * 1000) {
            addCount = 1;
        }
        mOnResumeTime = 0;

        addRunCount(addCount);

        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
        SoundPlayer.GetInstance(mContext).setPaused(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
