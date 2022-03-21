package com.chuyu.face.http.nohttp;
/*
 * Copyright 2015 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.text.TextUtils;

import com.chuyu.face.http.Code;
import com.chuyu.face.http.NoLoadingConnectTask;
import com.chuyu.face.http.NetResult;
import com.chuyu.face.tools.JsonUtil;
import com.chuyu.face.tools.LogUtil;
import com.yanzhenjie.nohttp.error.NetworkError;
import com.yanzhenjie.nohttp.error.NotFoundCacheError;
import com.yanzhenjie.nohttp.error.TimeoutError;
import com.yanzhenjie.nohttp.error.URLError;
import com.yanzhenjie.nohttp.error.UnKnownHostError;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ProtocolException;


public class HttpNoLoadingResponseListener<T> implements OnResponseListener<T> {
	private static String TAG = "XD_log";

	/**
	 * Request.
	 */
	// private Request<?> mRequest;
	/**
	 * 结果回调.
	 */
	private NoLoadingConnectTask<Object> callback;

	/**
	 *
	 * @param request
	 *            请求对象.
	 * @param callback
	 *            回调对象.
	 */
	public HttpNoLoadingResponseListener(Request<JSONObject> request, NoLoadingConnectTask<Object> callback) {
		// this.mRequest = request;
		this.callback = callback;
	}

	/**
	 * 开始请求
	 */
	@Override
	public void onStart(int what) {
		callback.openLoading();
	}

	/**
	 * 结束请求
	 */
	@Override
	public void onFinish(int what) {
		callback.closeLoading();
	}


	/**
	 * 成功回调.
	 */
	@Override
	public void onSucceed(int what, Response<T> res) {
		try {
			if (callback != null) {
				if (res == null) {
					callback.onFailure(Code.EXCEPTION_ERROR, "");
					return;
				}
				String jsonrs=res.get().toString();
				LogUtil.e(TAG, "接口返回："+jsonrs);
				if(TextUtils.isEmpty(jsonrs)){
					callback.onFailure(Code.EXCEPTION_ERROR, "");
					return;
				}
				JSONObject response = new JSONObject(jsonrs);

				int rsCodeBol=response.optInt("code");
				String eMsg = response.optString("msg");
				int rsCode=Code.FAIL;
				if(rsCodeBol==0){
					rsCode=Code.SUCCEED;
				}
				if (rsCode == Code.SUCCEED) {// 成功
					try {
						JSONObject rsSingleBean = response.optJSONObject("dataSingle");
						JSONObject listBeanObj= response.optJSONObject("dataArray");
						if (rsSingleBean != null) {
							Object t = JsonUtil.jsonToResult(rsSingleBean.toString(), callback.doGetBackClass());
							callback.onSuccess(t, rsCode,eMsg);
						} else if (listBeanObj != null) {
							JSONArray rsListBean=listBeanObj.optJSONArray("dataList");
							Object t = JsonUtil.jsonToResult(rsListBean.toString(), callback.doGetBackClass());
							callback.onSuccess(t,rsCode, eMsg);
						} else {
							NetResult netResult = new NetResult();
							netResult.setErr_code(rsCode+"");
							netResult.setErr_msg(eMsg);
							callback.onSuccess(netResult,rsCode, eMsg);
						}
					} catch (Exception e) {// 失败
						e.printStackTrace();
						callback.onSuccess(null,Code.DATA_ERROR, "");
					}
				} else {// 失败
					callback.onFailure(rsCode, eMsg);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			callback.onSuccess(null,Code.EXCEPTION_ERROR, "");
		}
	}

	/**
	 * 失败回调.
	 */
	@Override
	public void onFailed(int what, Response<T> res) {
		try {
			Exception exception = res.getException();
			if (exception instanceof NetworkError) {// 网络不好
				LogUtil.e(TAG, "请检查网络。");
                callback.onFailure(Code.NET_NOT_CONN, "");
			} else if (exception instanceof TimeoutError) {// 请求超时
				LogUtil.e(TAG, "请求超时，网络不好或者服务器不稳定。");
                callback.onFailure(Code.NET_CONN_TIMEOUT, "");
			} else if (exception instanceof UnKnownHostError) {// 找不到服务器
				LogUtil.e(TAG, "未发现指定服务器，清切换网络后重试。");
			} else if (exception instanceof URLError) {// URL是错的
				LogUtil.e(TAG, "URL错误。");
                callback.onFailure(Code.URL_BROKEN, "");
			} else if (exception instanceof NotFoundCacheError) {
				// 这个异常只会在仅仅查找缓存时没有找到缓存时返回
				callback.onFailure(Code.NET_NOT_CONN, "");
			} else if (exception instanceof ProtocolException) {
				callback.onFailure(Code.NET_NOT_CONN, "");
			} else {
				callback.onFailure(Code.NET_NOT_CONN, "");
			}
			LogUtil.e(TAG, "错误：" + exception.getMessage());

		} catch (Exception e) {

		}
	}




}
