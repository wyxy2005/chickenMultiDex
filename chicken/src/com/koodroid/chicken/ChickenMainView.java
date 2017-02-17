package com.koodroid.chicken;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class ChickenMainView extends FrameLayout {
    private Context mContext = null;
    private BigChicken mBigChicken = null;
	private MainActivity mActivity = null;


	public void setActivity(MainActivity ac) {
		mActivity = ac;
	}


	public ChickenMainView(Context context) {
		super(context);
		initUI(context);
	}
	
	public ChickenMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }
	
	private void initUI(Context context) {
	    mContext = context;

	    ARHelper.checkPackageValidaty(context);
	    mBigChicken = new BigChicken(mContext);
	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mBigChicken,params);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
	    boolean ret = super.onTouchEvent(event);

	    if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        addEgg(event.getX(),event.getY());
	        moveBigChickenTo(event.getX(),event.getY());
            mActivity.setOnDownEgg();
	    }
	    
	    return ret;
	}
	
	void moveBigChickenTo(float x,float y) {
	    mBigChicken.startDownEgg();
	    mBigChicken.mX = (int)x;
	    mBigChicken.mY = (int)y;
	    SoundPlayer.GetInstance(mContext).stop(SoundPlayer.DOWN_EGG);
	    SoundPlayer.GetInstance(mContext).playSound(SoundPlayer.DOWN_EGG, 0);
	    //mBigChicken.openMouse();
	}
	
	void addEgg(float x,float y) {
	    EggView egg = new EggView(mContext);
	    egg.mX = (int)x;
	    egg.mY = (int)y;
	    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
	            ViewGroup.LayoutParams.WRAP_CONTENT);
	    int childCount = this.getChildCount();
	    addView(egg,childCount-1,params);
	}
	
	private boolean mFirstLayout = true;
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	    final int count = getChildCount();
	    for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final int width = child.getMeasuredWidth();
                final int height = child.getMeasuredHeight();
                int childLeft = 0;
                int childTop = 0;
                if (child instanceof EggView) {
                    EggView egg = (EggView)child;
                    childLeft = egg.mX - width/2;
                    childTop = egg.mY - height;
                } else {
                    BigChicken chicken = (BigChicken) child;
                    if (mFirstLayout){
                        chicken.mX = getMeasuredWidth()/2;
                        chicken.mY = getMeasuredHeight()/2 + height/2;
                        mFirstLayout = false;
                    }
                    childLeft = chicken.mX - width/2;
                    childTop = chicken.mY - height;
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
	    }
    }

}
