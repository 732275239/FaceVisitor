package com.chuyu.face.http.nohttp;


import com.chuyu.face.http.ConnectTask;
import com.chuyu.face.http.DownFileBean;
import com.chuyu.face.http.NoLoadingConnectTask;
import com.chuyu.face.http.SSLContextUtil;
import com.chuyu.face.tools.LogUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.download.DownloadRequest;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import javax.net.ssl.SSLContext;


/**
 * 
 * NoHttp请求网络控制类
 *
 */
public class NoHttpController {

	private static NoHttpController noHttpController;
	private static String TAG = "ywlj_log";
	/**
	 * 请求队列.
	 */
	private RequestQueue requestQueue;

	/**
	 * 下载队列.
	 */
	private static DownloadQueue downloadQueue;

	private final int DEAFULTWATH = 0x001;

	private NoHttpController() {
		requestQueue = NoHttp.newRequestQueue();
	}

	/**
	 * 创建请求队列.
	 */
	public synchronized static NoHttpController getRequestInstance() {
		if (noHttpController == null)
			noHttpController = new NoHttpController();
		return noHttpController;
	}

	/**
	 * 创建下载队列.
	 */
	public synchronized static NoHttpController getDownloadInstance() {

		if (noHttpController == null)
			noHttpController = new NoHttpController();
		if (downloadQueue == null)
			downloadQueue = NoHttp.newDownloadQueue(3);
		return noHttpController;
	}

	/**
	 * 
	 * 使用NoHttp请求网络（请求网络获取数据、上传文件）
	 * 
	 * @param <T>
	 * @param <T>
	 * @param url
	 *            接口地址
	 * @param params
	 *            接口参数
	 * @param callback
	 *            接口回调
	 * @param httpType
	 *            0:http，1:https(不带证书)，2:https(带证书)
	 */
	public <T> void requestNet(String url, Map<String, Object> params, final ConnectTask callback, int httpType) {
		Request<JSONObject> request = NoHttpRequest.getRequestInstance().postFileOrStringRequest(url, params);
		SSLContext sslContext;
		try {
			switch (httpType) {
			case 0:
				break;
			case 1:
				sslContext = SSLContextUtil.getDefaultSLLContext();
				if (sslContext != null) {
					request.setSSLSocketFactory(sslContext.getSocketFactory());
				}
				request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
				break;
			case 2:
				sslContext = SSLContextUtil.getSSLContext();
				if (sslContext != null) {
					request.setSSLSocketFactory(sslContext.getSocketFactory());
				}
				break;
			}
		} catch (Exception e) {

			LogUtil.d(TAG, "SSLContext异常！");
		}
		HttpResponseListener<T> responseListener = new HttpResponseListener<T>(request, callback);
		requestQueue.add(DEAFULTWATH, (Request<T>) request, responseListener);
	}
/**
	 *
 * 不带loading的网络请求
	 * 使用NoHttp请求网络（请求网络获取数据、上传文件）
	 *
	 * @param <T>
	 * @param <T>
	 * @param url
	 *            接口地址
	 * @param params
	 *            接口参数
	 * @param callback
	 *            接口回调
	 * @param httpType
	 *            0:http，1:https(不带证书)，2:https(带证书)
	 */
	public <T> void requestNet(String url, Map<String, Object> params, final NoLoadingConnectTask callback, int httpType) {
		Request<JSONObject> request = NoHttpRequest.getRequestInstance().postFileOrStringRequest(url, params);
		SSLContext sslContext;
		try {
			switch (httpType) {
			case 0:
				break;
			case 1:
				sslContext = SSLContextUtil.getDefaultSLLContext();
				if (sslContext != null) {
					request.setSSLSocketFactory(sslContext.getSocketFactory());
				}
				request.setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
				break;
			case 2:
				sslContext = SSLContextUtil.getSSLContext();
				if (sslContext != null) {
					request.setSSLSocketFactory(sslContext.getSocketFactory());
				}
				break;
			}
		} catch (Exception e) {

			LogUtil.d(TAG, "SSLContext异常！");
		}
		HttpNoLoadingResponseListener<T> responseListener = new HttpNoLoadingResponseListener<T>(request, callback);
		requestQueue.add(DEAFULTWATH, (Request<T>) request, responseListener);
	}

	/**
	 * 使用NoHttp下载文件
	 * 
	 * @param downFileBean
	 *            （下载地址、保存路径的集合）
	 * @param downloadListener
	 *            （下载监听）
	 */
	public void downLoadNet(ArrayList<DownFileBean> downFileBean, NoHttpDownLoadListener downloadListener) {

		if (downFileBean == null) {
			LogUtil.d(TAG, "缺少下载地址、保存路径！");
		}
		if (downloadListener == null) {
			LogUtil.d(TAG, "缺少下载监听！");
		}
		for (int i = 0; i < downFileBean.size(); i++) {
			// 创建四个下载请求并且保存起来。
			DownloadRequest downloadRequest = NoHttp.createDownloadRequest(downFileBean.get(i).getDownUrl(),
					downFileBean.get(i).getSavePath(), downFileBean.get(i).getFileName(), true, true);
			downloadQueue.add(i, downloadRequest, downloadListener);
		}

	}

	/**
	 * 取消这个sign标记的所有请求.
	 */
	public void cancelBySign(Object sign) {
		requestQueue.cancelBySign(sign);
	}

	/**
	 * 取消队列中所有请求.
	 */
	public void cancelAll() {
		requestQueue.cancelAll();
	}

	/**
	 * 退出app时停止所有请求.
	 */
	public void stopAll() {
		requestQueue.stop();
	}

}
