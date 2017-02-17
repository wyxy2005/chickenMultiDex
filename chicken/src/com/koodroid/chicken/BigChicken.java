package com.koodroid.chicken;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class BigChicken extends FrameLayout implements AnimatorListener {
    private Context mContext = null;
    
    public static final int BIG_CHICKEN_TYPE = 1;
    public static final int LITTER_CHICKEN_TYPE = 2;
    
    int mChickenType = BIG_CHICKEN_TYPE;
    
    boolean mRemoved = false;
    
    private static final int OPEN_MOUTH_MSG = 1;
    
    int mOpenMouthIntervals = 12000;
    
    int mX;
    int mY;
    
    private ImageView mMain = null;
    private ImageView mMouth = null;
    private ImageView mEye = null;
    private ImageView mLeft = null;
    private ImageView mRight = null;
    
    private Drawable mMouthDrawable = null ;
    private Drawable mMouthOpenDrawable = null ;
    
    private AnimatorSet mDownEggSet = null;
    
    Handler handler = new Handler() {  
        public void handleMessage(Message msg) {
            switch(msg.what){
            case OPEN_MOUTH_MSG:
                openMouse();
                break;
            default:
                break;
            }
        };  
    };  
    
    

	public BigChicken(Context context) {
		super(context);
		initUI(context);
	}
	
	public BigChicken(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }
	
    public void startDownEgg() {
        final int transDuration = 100;
        if (mDownEggSet == null) {
            final int height = this.getMeasuredHeight();
            final int width = this.getMeasuredWidth();
            ObjectAnimator objectAnimator0 = ObjectAnimator.ofFloat(this, "translationX", 0, 0);
            objectAnimator0.setDuration(0);
            ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(this, "translationY",
                    -height / 2);
            objectAnimator1.setDuration(transDuration);
            ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(this, "translationX", 0,
                    width / 2);
            objectAnimator2.setDuration(transDuration);
            ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(this, "translationY", 0);
            objectAnimator3.setDuration(transDuration);

            mDownEggSet = new AnimatorSet();
            mDownEggSet.playSequentially(objectAnimator0, objectAnimator1, objectAnimator2,
                    objectAnimator3);

            mDownEggSet.setDuration(3 * transDuration);
            mDownEggSet.addListener(this);
        } else {
            mDownEggSet.cancel();
        }
        this.setTranslationY(0);
        this.setTranslationX(0);
        mDownEggSet.start();
    }
	
	private void initUI(Context context) {
	    mContext = context;
	    LayoutInflater.from(context).inflate(R.layout.big_chicken, this);
	    mMain = (ImageView) findViewById(R.id.main_part);
	    mMouth = (ImageView) findViewById(R.id.mouth);
	    mEye = (ImageView) findViewById(R.id.eye);
	    mLeft = (ImageView) findViewById(R.id.left);
	    mRight = (ImageView) findViewById(R.id.right);
	    
	    mMouthDrawable = mContext.getResources().getDrawable(R.drawable.mouth);
	    mMouthOpenDrawable = mContext.getResources().getDrawable(R.drawable.mouth_open);
	    
	    startLegAnimation();
	}
	
	private void startLegAnimation() {
	    final int transDuration = 500;
	    ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(mLeft, "translationY", 0, -10);
	    objectAnimator1.setDuration(transDuration);
	    ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(mRight, "translationY", 0, -10);
	    objectAnimator2.setDuration(transDuration);
	    objectAnimator2.setStartDelay(transDuration/3*2);

        objectAnimator1.setRepeatCount(Animation.INFINITE);
        objectAnimator2.setRepeatCount(Animation.INFINITE);
	    
        AnimatorSet set = new AnimatorSet();
        set.play(objectAnimator1).with(objectAnimator2);
        
        set.setDuration(transDuration);
        set.start();
	}
	
	public void openMouse() {
	    handler.removeMessages(OPEN_MOUTH_MSG);
	    if (mRemoved)
	        return;
	    mMouth.setImageDrawable(mMouthOpenDrawable);
	    int soundType = SoundPlayer.LITTER_CHICKEN;
	    if (mChickenType == BIG_CHICKEN_TYPE){
	        soundType = SoundPlayer.BIG_CHICKEN;
	        SoundPlayer.GetInstance(mContext).stop(soundType);
	    } 
	    SoundPlayer.GetInstance(mContext).playSound(soundType, 0);
	    
	    mMouth.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMouth.setImageDrawable(mMouthDrawable);
                handler.sendEmptyMessageDelayed(OPEN_MOUTH_MSG, mOpenMouthIntervals);
            }
	    }, 500);
	}

    @Override
    public void onAnimationCancel(Animator arg0) {
        
    }

    @Override
    public void onAnimationEnd(Animator arg0) {
        handler.removeMessages(OPEN_MOUTH_MSG);
        handler.sendEmptyMessage(OPEN_MOUTH_MSG);
    }

    @Override
    public void onAnimationRepeat(Animator arg0) {
        
    }

    @Override
    public void onAnimationStart(Animator arg0) {
        
    }
	
	

}
