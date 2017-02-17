
package com.koodroid.chicken;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {

    public static final int LITTER_CHICKEN = 1;
    public static final int BIG_CHICKEN = 2;
    public static final int DOWN_EGG = 3;
    public static final int EGG_BROKE = 4;

    private static SoundPlayer mSoundPlayer = null;

    private Context mContext;
    private SoundPool mSoundPool = null;
    HashMap<Integer, Integer> mSoundPoolMap = new HashMap<Integer, Integer>();

    private boolean mPaused = false;
    static SoundPlayer GetInstance(Context context) {
        if (mSoundPlayer == null) {
            mSoundPlayer = new SoundPlayer(context);
        }
        return mSoundPlayer;
    }

    SoundPlayer(Context context) {
        mContext = context;
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundPoolMap.put(LITTER_CHICKEN, mSoundPool.load(mContext, R.raw.litter,1));
        mSoundPoolMap.put(BIG_CHICKEN, mSoundPool.load(mContext,R.raw.big,1));
        mSoundPoolMap.put(DOWN_EGG, mSoundPool.load(mContext,R.raw.down_egg,1));
        mSoundPoolMap.put(EGG_BROKE, mSoundPool.load(mContext,R.raw.egg_broke,1));
    }
    
    void setPaused(boolean pause) {
        mPaused = pause;
    }

    public void playSound(int sound, int loop) {
        if (mPaused)
            return;
        AudioManager mgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;
        int priority = 1;
        if (sound == LITTER_CHICKEN)
            priority = 0;
        mSoundPool.play(mSoundPoolMap.get(sound), volume, volume, priority, loop, 1f);
    }
    
    public void autoPause() {
        mSoundPool.autoPause();
    }
    
    public void autoResume() {
        mSoundPool.autoResume();
    }
    
    public void stop(int sound) {
        mSoundPool.stop(mSoundPoolMap.get(sound));
    }
}
