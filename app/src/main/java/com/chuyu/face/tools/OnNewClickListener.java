package com.chuyu.face.tools;

import android.view.View;

/**
 * Created by Zoello on 2018/10/26.
 * 所有onClick都建议使用这个点击监听，一秒内只能点击一次，防止恶意点击
 */

public abstract class OnNewClickListener implements View.OnClickListener {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    public abstract void onNewClick(View v);

    @Override
    public void onClick(View v) {
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime;
            onNewClick(v);
        }
    }

}

