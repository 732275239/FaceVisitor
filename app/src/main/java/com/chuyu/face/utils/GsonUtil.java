package com.chuyu.face.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;


public class GsonUtil {
    private static Gson mGson;

    static {
        if (mGson == null) {
            mGson = new Gson();
        }
    }

    public static <T> T GsonToBean(String str, Class<T> Class) {
        T t = null;
        if (mGson != null) {
            t = mGson.fromJson(str, Class);
        }
        return t;
    }

    public static <T> T GsonToBean(String str, Type type) {
        T t = null;
        if (mGson != null) {
            t = mGson.fromJson(str, type);
        }
        return t;
    }

    public static <T> String ToGson(List<T> list) {
        String str = "";
        if (mGson != null) {
            str = mGson.toJson(list);
        }
        return str;
    }

    public static <T> String ToGson(T bean) {
        String str = "";
        if (mGson != null) {
            str = mGson.toJson(bean);
        }
        return str;
    }

    public static String getKey(String res, String key) {
        JSONObject jsonObject = null;
        String a = "";
        try {
            jsonObject = new JSONObject(res);
            a = jsonObject.optString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(a)) {
            return null;
        }
        return a;
    }
}
