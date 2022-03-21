package com.chuyu.face.http.nohttp;


import com.chuyu.face.tools.LogUtil;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

/**
 * 创建NoHttp请求参数
 * 
 * @author Administrator
 * 
 */
public class NoHttpRequest {

	private static String TAG = "no_http";
	private static NoHttpRequest noHttpRequest;

	public synchronized static NoHttpRequest getRequestInstance() {
		if (noHttpRequest == null)
			noHttpRequest = new NoHttpRequest();
		return noHttpRequest;
	}

	/**
	 * 
	 * @param url
	 *            请求路径
	 * @param parameMap
	 *            请求参数-键值对（支持file 和 string）
	 */
	public Request<JSONObject> postFileOrStringRequest(final String url, Map<String, Object> parameMap) {
		Request<JSONObject> request = NoHttp.createJsonObjectRequest(url,RequestMethod.POST);
		//Request<String> request = NoHttp.createStringRequest(url, RequestMethod.POST);
		LogUtil.d(TAG, "请求地址:" + url);
		if (null != parameMap && parameMap.size() > 0) {
			LogUtil.d(TAG, "请求参数:" + parameMap.toString());
			Iterator entries = parameMap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				String key = (String) entry.getKey();
				Object value = entry.getValue();
				if (value instanceof File) {
//					BasicBinary binary = new FileBinary((File) value);
				FileBinary fileBinary = new FileBinary((File) value);
					request.add(key, fileBinary);
				} else {
					request.add(key, value + "");
				}
			}
		}
		// 添加请求头
       // request.addHeader("Author", "nohttp_sample");
        //请求服务器成功则返回服务器数据，如果请求服务器失败，读取缓存数据返回。
        //request.setCacheMode(CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE);

		/**
		 * 上传文件；上传文件支持File、Bitmap、ByteArrayBinary、InputStream四种，这里推荐File、
		 * InputStream。
		 */
		// request.add("userHead", new FileBinary());
		// request.add("userHead", new BitmapBinary());
		// request.add("userHead", new ByteArrayBinary());
		// request.add("", new InputStreamBinary());

//		request.setConnectTimeout(10 * 1000);// 设置连接超时。
//		request.setReadTimeout(10 * 1000); // 设置读取超时时间，也就是服务器的响应超时。
//        request.setRetryCount(3);
		return request;

	}

}
