package com.chuyu.face.http;

import android.app.Activity;
import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.chuyu.face.base.ApplicationContext;
import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.tools.StringUtil;
import com.chuyu.face.tools.ToastUtil;


public abstract class NoLoadingConnectTask<T> {
    //不带loading的task
    protected Activity mActivity;
    protected TypeToken<T> rsType;
    private Context mContext;

    public NoLoadingConnectTask(TypeToken<T> typeToken, Activity mContext) {
        this.mContext = mContext;
        BaseActivity baseActivity = (BaseActivity) mContext;
        this.mActivity = baseActivity;
        this.rsType = typeToken;

        openLoading();
    }


    public void openLoading() {
        if (mContext != null) {
        }
    }

    /**
     * 关闭loading对话框
     */
    public void closeLoading() {
        if (mContext != null) {
        }
    }


    public void onSuccess(T rsData, int eCode, String eMsg) {
        showErrorTips(eCode, eMsg);
    }


    public void onFailure(int eCode, String eMsg) {
        showErrorTips(eCode, eMsg);
    }

    public void showErrorTips(int eCode, String eMsg) {
        closeLoading();
        if (mActivity == null) {
            return;
        }
        switch (eCode) {
            case Code.SUCCEED:

                break;
            case Code.EXCEPTION_ERROR:
                ToastUtil.showTips(mActivity, "服务器连接超时！");
                break;
            case Code.NET_NOT_CONN:
                ToastUtil.showTips(mActivity, "网络连接错误！");
                break;
            case Code.NET_CONN_TIMEOUT:
                ToastUtil.showTips(mActivity, "网络连接超时！");
                break;
            case Code.URL_BROKEN:
                ToastUtil.showTips(mActivity, "url错误！");
                break;
            case Code.USER_NO_LOGIN:
                ToastUtil.showTips(mActivity, "登录超时，请重新登录！");
                ApplicationContext.getInstance().getSpTools().clearUser();
                break;
            case Code.JSON_ERROR:
                ToastUtil.showTips(mActivity, "数据解析错误！");
                break;

            case Code.DATA_ERROR:
                ToastUtil.showTips(mActivity, "数据填充异常！");
                break;

            default:
                if (!StringUtil.isEmpty(eMsg)) {// 其它错误
                    ToastUtil.showTips(mActivity, eMsg);
                } else {
                    ToastUtil.showTips(mActivity, "错误码：" + eCode);
                }
                break;
        }
    }

    public TypeToken<T> doGetBackClass() {
        return this.rsType;
    }
}
