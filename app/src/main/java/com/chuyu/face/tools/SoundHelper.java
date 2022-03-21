package com.chuyu.face.tools;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.SparseIntArray;

public final class SoundHelper {
    private static SparseIntArray mRawSounds = new SparseIntArray();

    private static LoadCompleteListener mListener = new LoadCompleteListener();

    private static final SoundPool sSoundPool = new SoundPool(1,
            AudioManager.STREAM_MUSIC, 0);

    static {
        sSoundPool.setOnLoadCompleteListener(mListener);
    }

    private SoundHelper() {
    }

    private static class LoadCompleteListener implements OnLoadCompleteListener {

        @Override
        public void onLoadComplete(final SoundPool soundPool,
                                   final int sampleId, final int status) {
            soundPool.play(sampleId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    private static int loadSound(final Context context, final int resId) {
        final Integer sound = sSoundPool.load(context, resId, 1);
        mRawSounds.put(resId, sound);
        return sound;
    }

    public static void playSound(final Context context, final int soundID) {
        synchronized (sSoundPool) {
            int sound = mRawSounds.get(soundID);
            if (sound == 0) {
                sound = loadSound(context, soundID);
            } else {
                sSoundPool.play(sound, 1.0f, 1.0f, 1, 0, 1.0f);
            }
        }
    }
}