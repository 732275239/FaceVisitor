package com.chuyu.face.cusview.recyclerview;

import android.content.Context;
import android.widget.FrameLayout;

public abstract class LoadMoreView extends FrameLayout {

    public LoadMoreView(Context context) {
        super(context);
    }

    public abstract void showLoading();

    public abstract void showRetry();

    public abstract void showEnd();

    public abstract boolean isLoadMoreEnable();

    public abstract void setOnRetryClickListener(OnRetryClickListener listener);
}
