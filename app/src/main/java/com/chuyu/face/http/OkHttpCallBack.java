package com.chuyu.face.http;


import com.google.gson.reflect.TypeToken;

public abstract class OkHttpCallBack<T> {

    protected TypeToken<T> rsType;

    public OkHttpCallBack(TypeToken<T> typeToken) {
        this.rsType = typeToken;
    }

    public void onSuccess(T rsData, String eCode, String eMsg) {
    }


    public void onFailure(String eCode, String eMsg) {
    }


    public void onError() {
    }

    public TypeToken<T> doGetBackClass() {
        return this.rsType;
    }
}
