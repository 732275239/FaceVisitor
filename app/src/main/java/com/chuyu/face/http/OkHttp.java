package com.chuyu.face.http;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.chuyu.face.base.URLs;
import com.chuyu.face.cusview.LoadingDailog;
import com.chuyu.face.tools.JsonUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Zoello
 * @description: 网络请求第三次封装
 * @date : 2021/4/14 11:27
 */
public class OkHttp {
    public static <T> void postSingeCode4111(final Context context, final Boolean asload, String URL,
                                             String params, final OkHttpCallBack<T> callback) {
        String token = "5d46cb8f9c89e0c85901be97fb446e4a7652510d";
        Log.i("http", "请求接口: " + URLs.HOST_URL + URL);
        Log.i("http", "params: " + params + "token" + token);
        OkHttpUtils.post()
                .url(URLs.HOST_URL + URL)
                .addParams("data", params)
                .addParams("appToken", token)
                .build()
                .writeTimeOut(200000)
                .readTimeOut(200000)
                .connTimeOut(2000)
                .execute(new com.zhy.http.okhttp.callback.StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                        callback.onError();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        Log.i("http", "返回: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("0000")) {
                                String data = jsonObject.getString("data");
                                if (data.isEmpty()) {
                                    NetResult netResult = new NetResult();
                                    netResult.setErr_code(code);
                                    netResult.setErr_msg(jsonObject.getString("msg"));
                                    callback.onSuccess((T) netResult, code, jsonObject.getString("msg"));
                                } else {
                                    Object t = JsonUtil.jsonToResult(data, callback.doGetBackClass());
                                    callback.onSuccess((T) t, code, jsonObject.getString("msg"));
                                }
                            } else if (code.equals("4111")) {
                                callback.onFailure(code, response);
                            } else {
                                callback.onFailure(code, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError();
                        }
                    }
                });
    }


    public static <T> void postFile(final Context context, String URL,
                                    File file, final OkHttpCallBack<T> callback) {
        String token = "5d46cb8f9c89e0c85901be97fb446e4a7652510d";
        Log.i("http", "请求接口: " + URLs.HOST_URL + URL);
        Log.i("http", "token: " + token);
        OkHttpClient mOkHttpClent = new OkHttpClient();
        //咸宁文件服务器
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        //1+14
//        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("appToken", token)
                .addFormDataPart("file", file.getName(), fileBody);
        MultipartBody build = requestBody.build();
        Request request = new Request.Builder()
                .url(URLs.HOST_URL + URL)
                .post(build)
                .build();

        OkHttpClient build1 = mOkHttpClent.newBuilder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        Call call = build1.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError();
                Log.e("abc", "请求失败");
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String string = response.body().string();
                    Log.i("http", "返回: " + string);
                    JSONObject jsonObject = new JSONObject(string);
                    String code = jsonObject.getString("code");
                    if (code.equals("0000")) {
                        String data = jsonObject.getString("data");
                        if (data.isEmpty()) {
                            NetResult netResult = new NetResult();
                            netResult.setErr_code(code);
                            netResult.setErr_msg(jsonObject.getString("msg"));
                            callback.onSuccess((T) netResult, code, jsonObject.getString("msg"));
                        } else {
                            Object t = JsonUtil.jsonToResult(data, callback.doGetBackClass());
                            callback.onSuccess((T) t, code, jsonObject.getString("msg"));
                        }
                    } else {
                        callback.onFailure(code, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onError();
                }

            }
        });


    }

    public static <T> void postSinge(final Context context, final Boolean asload, String URL,
                                     String params, final OkHttpCallBack<T> callback) {

        String token = "5d46cb8f9c89e0c85901be97fb446e4a7652510d";
        Log.i("http", "请求接口: " + URLs.HOST_URL + URL);
        Log.i("http", "params: " + params + "token" + token);
        OkHttpUtils.post()
                .url(URLs.HOST_URL + URL)
                .addParams("data", params)
                .addParams("appToken", token)
                .build()
                .writeTimeOut(200000)
                .readTimeOut(200000)
                .connTimeOut(2000)
                .execute(new com.zhy.http.okhttp.callback.StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (asload) {
                        }
                        callback.onError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (asload) {
                        }
                        Log.i("http", "返回: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("0000")) {
                                String data = jsonObject.getString("data");
                                if (data.isEmpty()) {
                                    NetResult netResult = new NetResult();
                                    netResult.setErr_code(code);
                                    netResult.setErr_msg(jsonObject.getString("msg"));
                                    callback.onSuccess((T) netResult, code, jsonObject.getString("msg"));
                                } else {
                                    Object t = JsonUtil.jsonToResult(data, callback.doGetBackClass());
                                    callback.onSuccess((T) t, code, jsonObject.getString("msg"));
                                }
                            } else {
                                callback.onFailure(code, jsonObject.getString("msg"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onError();
                        }
                    }
                });
    }

    public static <T> void postArray(final Activity context, final Boolean asload, String URL,
                                     String params, final OkHttpCallBack<T> callback) {

        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(context)
                .setCancelable(false)
                .setMessage("请稍等...")
                .setCancelOutside(false);
        LoadingDailog dialog = loadBuilder.create();
        if (asload) {
            dialog.show();
        }
        String token = "5d46cb8f9c89e0c85901be97fb446e4a7652510d";
        Log.i("http", "请求接口: " + URLs.HOST_URL + URL);
        Log.i("http", "params: " + params + "token" + token);
        OkHttpUtils.post()
                .url(URLs.HOST_URL + URL)
                .addParams("data", params)
                .addParams("appToken", token)
                .build()
                .writeTimeOut(60000)
                .readTimeOut(60000)
                .connTimeOut(2000)
                .execute(new com.zhy.http.okhttp.callback.StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (asload) {
                            dialog.dismiss();
                        }
                        callback.onError();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("http", "返回: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("0000")) {
                                JSONArray rsListBean = jsonObject.optJSONArray("data");
                                Object t = JsonUtil.jsonToResult(rsListBean.toString(), callback.doGetBackClass());
                                callback.onSuccess((T) t, code, jsonObject.getString("msg"));
                            } else {
                                callback.onFailure(code, jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (asload) {
                            dialog.dismiss();
                        }
                    }
                });
    }


}
