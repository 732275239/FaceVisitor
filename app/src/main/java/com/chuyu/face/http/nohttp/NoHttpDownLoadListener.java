package com.chuyu.face.http.nohttp;


import com.chuyu.face.tools.LogUtil;
import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.ServerError;
import com.yanzhenjie.nohttp.error.StorageReadWriteError;
import com.yanzhenjie.nohttp.error.StorageSpaceNotEnoughError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;

import java.util.Locale;

/**
 * 下载状态监听。
 */
public class NoHttpDownLoadListener implements DownloadListener {
	private static String TAG = "no_http";
	@Override
	public void onStart(int what, boolean isResume, long beforeLength, Headers headers, long allCount) {
		if (allCount != 0) {
			int progress = (int) (beforeLength * 100 / allCount);
		}

	}



	@Override
	public void onDownloadError(int what, Exception exception) {
		LogUtil.e(TAG, exception.getMessage());
		String message = "下载出错了：%1$s";
		String messageContent;
		if (exception instanceof ServerError) {
			messageContent = "服务器数据错误！";
		} else if (exception instanceof NetworkError) {
			messageContent = "网络不可用，请检查网络！";
		} else if (exception instanceof StorageReadWriteError) {
			messageContent = "存储卡错误，请检查存储卡！";
		} else if (exception instanceof StorageSpaceNotEnoughError) {
			messageContent = "存储卡空间不足！";
		} else if (exception instanceof TimeoutError) {
			messageContent = "下载超时！";
		} else if (exception instanceof UnKnownHostError) {
			messageContent = "找不到服务器。";
		} else if (exception instanceof URLError) {
			messageContent = "URL地址错误。";
		} else {
			messageContent = "未知错误。";
		}
		message = String.format(Locale.getDefault(), message, messageContent);

	}
	@Override
	public void onProgress(int what, int progress, long fileCount, long speed) {

	}

	@Override
	public void onFinish(int what, String filePath) {

	}

	@Override
	public void onCancel(int what) {
		
	}

}
