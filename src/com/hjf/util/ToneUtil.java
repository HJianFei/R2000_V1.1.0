package com.hjf.util;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;

import com.hjf.r2000.R;

public class ToneUtil {
	private static ToneUtil sInstance = null;
	private SoundPool mSoundPool = null;
	private int mIsoundId = 0;
	private ToneGenerator mToneGenerator;
	private final int DURATION_MS = 20;
	public static final int TYPE_SYSTEM = 1;
	public static final int TYPE_CUSTOM = 2;

	public static ToneUtil getInstace(Context context) {
		if (sInstance == null) {
			sInstance = new ToneUtil(context);
		}
		return sInstance;
	}

	private HashMap<Integer, Integer> mSpMap = null;

	@SuppressWarnings("deprecation")
	public ToneUtil(Context context) {
		mSpMap = new HashMap<Integer, Integer>();
		mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		/**
		 * �ĸ���ͬ���������֣������ֲ�һ����Ϊ���ܸ���ز���������soundPool֧��ͬʱ���Ŷ���������������ǲ�ͬ�������ļ���
		 * ͬһ�������ļ���û���꣬Ҫ���ڶ��εĻ���ֹͣ��һ�εĲ���
		 */
		mSpMap.put(0, mSoundPool.load(context, R.raw.tone0, 1));
		mSpMap.put(1, mSoundPool.load(context, R.raw.tone1, 1));
		mSpMap.put(2, mSoundPool.load(context, R.raw.tone2, 1));
		mSpMap.put(3, mSoundPool.load(context, R.raw.tone3, 1));
		mToneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, 100);
	}

	int i = 1;

	public void play(int type) {
		switch (type) {
		case TYPE_SYSTEM:
			if (mToneGenerator != null) {
				mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2,
						DURATION_MS);
			}
			break;
		case TYPE_CUSTOM:
			i++;
			mSoundPool.play(mSpMap.get(i % 4), 1, 1, 1, 0, 1);

			if (i >= Integer.MAX_VALUE) {
				i = 1;
			}
			break;
		default:
			break;
		}
	}

	public void release() {
		if (mToneGenerator != null) {
			mToneGenerator = null;
		}

		if (mSoundPool != null) {
			mSoundPool.release();
			mSoundPool = null;
		}
		if (sInstance != null) {
			sInstance = null;
		}
	}

}
