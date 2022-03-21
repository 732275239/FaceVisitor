package com.chuyu.face.tools;

import android.text.TextUtils;
import android.util.Log;

import com.chuyu.face.base.AppConfigs;


/**
 *名称：Log统一管理类
 *创建人：xw
 *创建时间：2017/3/17 0017 下午 2:14
 *详细说明：
 */
public class LogUtil {

	private LogUtil() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static boolean isDebug = AppConfigs.CHECK_DEBUG_ENABLE;// 是否需要打印bug，可以在application的onCreate函数里面初始化
	private static final String TAG = AppConfigs.TAG;

	// 下面四个是默认tag的函数
	public static void i(String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(TAG, "empty message");
			else
				Log.i(TAG, msg);

		}
	}

	public static void d(String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(TAG, "empty message");
			else
				Log.d(TAG, msg);
		}
	}

	public static void e(String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(TAG, "empty message");
			else
				Log.e(TAG, msg);
		}
	}

	public static void v(String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(TAG, "empty message");
			else
				Log.v(TAG, msg);
		}
	}

	// 下面是传入自定义tag的函数
	public static void i(String tag, String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(tag, "empty message");
			else
				Log.i(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(tag, "empty message");
			else
				Log.d(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(tag, "empty message");
			else
				Log.e(tag, msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isDebug) {
			if (TextUtils.isEmpty(msg))
				Log.i(tag, "empty message");
			else
				Log.v(tag, msg);
		}
	}
}
