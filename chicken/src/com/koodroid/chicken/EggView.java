package com.koodroid.chicken;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class EggView extends FrameLayout implements AnimatorListener{
    private Context mContext = null;
    private static final int DELAY_EGG_BROKE = 8000;
    private ImageView mEgg = null;;
    private BigChicken mLitter;
    int mX;
    int mY;
    
    private boolean mRemoved = false;
    
    private ObjectAnimator mFirstAnimator;
    private ObjectAnimator mSecondAnimator;
    
    private ObjectAnimator mChickenMoveAnimator;
    

	public EggView(Context context) {
		super(context);
		initUI(context);
	}
	
	public EggView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }
	
	private void initUI(Context context) {
	    mContext = context;
	    mEgg = new ImageView(mContext);
	    mEgg.setImageResource(R.drawable.egg);
	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mEgg,params);
	    
	    mFirstAnimator = ObjectAnimator.ofFloat(this, "rotation",-30,30,0,-30,30,0);
	    mFirstAnimator.setStartDelay(DELAY_EGG_BROKE);
	    mFirstAnimator.setDuration(500);
	    mFirstAnimator.start();
	    mFirstAnimator.addListener(this);
	    
	}

    @Override
    public void onAnimationCancel(Animator arg0) {
        
    }

    @Override
    public void onAnimationEnd(Animator arg0) {
        if (mFirstAnimator == arg0) {
            mEgg.setImageResource(R.drawable.egg_broke);
            SoundPlayer.GetInstance(mContext).playSound(SoundPlayer.EGG_BROKE, 0);
            mSecondAnimator = ObjectAnimator.ofFloat(this, "rotation",0,-30,30,0);
            mSecondAnimator.setStartDelay(200);
            mSecondAnimator.setDuration(200);
            mSecondAnimator.start();
            mSecondAnimator.addListener(this);
        } else if (mSecondAnimator == arg0) {
            mLitter = new BigChicken(mContext);
            mLitter.mChickenType = BigChicken.LITTER_CHICKEN_TYPE;
            mLitter.setScaleX(0.5f);
            mLitter.setScaleY(0.5f);
            this.removeAllViews();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            addView(mLitter,params);
            mLitter.mOpenMouthIntervals = 1000;
            mLitter.openMouse();
            
            DisplayMetrics dis = mContext.getResources().getDisplayMetrics();
            int width = dis.widthPixels;
            float widthdp = width / dis.density;
            
            int duration = (int)widthdp * 10;
            
            mChickenMoveAnimator = ObjectAnimator.ofFloat(this, "translationX",0,-width);
            mChickenMoveAnimator.setStartDelay(100);
            mChickenMoveAnimator.setDuration(duration);
            mChickenMoveAnimator.start();
            mChickenMoveAnimator.addListener(this);
        } else if (mChickenMoveAnimator == arg0) {
            ViewGroup parent = (ViewGroup)this.getParent();
            parent.removeView(this);
            mRemoved = true;
            mLitter.mRemoved = true;
        }
        
    }

    @Override
    public void onAnimationRepeat(Animator arg0) {
        
    }

    @Override
    public void onAnimationStart(Animator arg0) {
        
    }
	
	
	
}
