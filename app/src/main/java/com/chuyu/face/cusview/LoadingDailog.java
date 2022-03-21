package com.chuyu.face.cusview;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chuyu.face.R;


public class LoadingDailog extends Dialog {
    public LoadingDailog(Context context) {
        super(context);
    }

    public LoadingDailog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private Context context;
        private String message;
        private boolean isShowMessage = true;
        private boolean isCancelable = false;
        private boolean isCancelOutside = false;

        public Builder(Context context) {
            this.context = context;

        }

        public LoadingDailog.Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public LoadingDailog.Builder setShowMessage(boolean isShowMessage) {
            this.isShowMessage = isShowMessage;
            return this;
        }

        public LoadingDailog.Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public LoadingDailog.Builder setCancelOutside(boolean isCancelOutside) {
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        public LoadingDailog create() {
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View view = inflater.inflate(R.layout.dialog_loading, (ViewGroup) null);
            LoadingDailog loadingDailog = new LoadingDailog(this.context, R.style.common_loading_dialog_style);
            TextView msgText = (TextView) view.findViewById(R.id.tipTextView);
            LottieAnimationView lottie = (LottieAnimationView) view.findViewById(R.id.lottie);
            lottie.useHardwareAcceleration();
            lottie.setAnimation("viewhelper/loading.json");
            lottie.loop(true);
            lottie.playAnimation();
            if (this.isShowMessage) {
                msgText.setText(this.message);
            } else {
                msgText.setVisibility(View.GONE);
            }
            loadingDailog.setContentView(view);
            loadingDailog.setCancelable(this.isCancelable);
            loadingDailog.setCanceledOnTouchOutside(this.isCancelOutside);
            Window window =  loadingDailog.getWindow();
            if (loadingDailog != null && window != null) {
                WindowManager.LayoutParams attr = window.getAttributes();
                if (attr != null) {
                    attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    attr.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    attr.gravity = Gravity.CENTER;//设置dialog 在布局中的位置
                }
            }
            return loadingDailog;
        }
    }
}
