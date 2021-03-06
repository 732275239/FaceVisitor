/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuyu.face.cusview.loading;


import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chuyu.face.R;

/**
 * 名称：Loading布局控制器
 * 详细说明：
 */
public class VaryViewHelperController {

    private IVaryViewHelper helper;

    public VaryViewHelperController(View view) {
        this(new VaryViewHelper(view));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        super();
        this.helper = helper;
    }

    public void showNetworkError(View.OnClickListener onClickListener) {
//        View layout = helper.inflate(R.layout.networkerror);
//        TextView textView = (TextView) layout.findViewById(R.id.message_info);
//        textView.setText(helper.getContext().getResources().getString(R.string.common_no_network_msg));
//
//        ImageView imageView = (ImageView) layout.findViewById(R.id.message_icon);
//        imageView.setImageResource(R.mipmap.networkerror);
//
//        if (null != onClickListener) {
//            layout.setOnClickListener(onClickListener);
//        }
        View layout = helper.inflate(R.layout.messagelottie);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        textView.setText(helper.getContext().getResources().getString(R.string.common_no_network_msg));
        LottieAnimationView lottieAnimationView = (LottieAnimationView) layout.findViewById(R.id.lottie);
        lottieAnimationView.useHardwareAcceleration();
        lottieAnimationView.setAnimation("viewhelper/networkerror.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
    }

    public void showError(String errorMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.messagelottie);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!TextUtils.isEmpty(errorMsg)) {
            textView.setText(errorMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }
        LottieAnimationView lottieAnimationView = (LottieAnimationView) layout.findViewById(R.id.lottie);
        lottieAnimationView.useHardwareAcceleration();
        lottieAnimationView.setAnimation("viewhelper/empty_box.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
//        View layout = helper.inflate(R.layout.message);
//        TextView textView = (TextView) layout.findViewById(R.id.message_info);
//        if (!TextUtils.isEmpty(errorMsg)) {
//            textView.setText(errorMsg);
//        } else {
//            textView.setText(helper.getContext().getResources().getString(R.string.common_error_msg));
//        }
//
//        if (null != onClickListener) {
//            layout.setOnClickListener(onClickListener);
//        }
//        helper.showLayout(layout);
    }

    public void showEmpty(String emptyMsg, View.OnClickListener onClickListener) {
        View layout = helper.inflate(R.layout.messagelottie);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!TextUtils.isEmpty(emptyMsg)) {
            textView.setText(emptyMsg);
        } else {
            textView.setText(helper.getContext().getResources().getString(R.string.common_empty_msg));
        }
        LottieAnimationView lottieAnimationView = (LottieAnimationView) layout.findViewById(R.id.lottie);
        lottieAnimationView.useHardwareAcceleration();
        lottieAnimationView.setAnimation("viewhelper/empty_box.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        if (null != onClickListener) {
            layout.setOnClickListener(onClickListener);
        }
        helper.showLayout(layout);
    }

    public void showLoading(String msg) {
//        View layout = helper.inflate(R.layout.loading);
//        if (!TextUtils.isEmpty(msg)) {
//            TextView textView = (TextView) layout.findViewById(R.id.loading_msg);
//            textView.setText(msg);
//        }
        View layout = helper.inflate(R.layout.messagelottie);
        TextView textView = (TextView) layout.findViewById(R.id.message_info);
        if (!TextUtils.isEmpty(msg)) {
            textView.setText(msg);
        }
        LottieAnimationView lottieAnimationView = (LottieAnimationView) layout.findViewById(R.id.lottie);
        lottieAnimationView.useHardwareAcceleration();
        lottieAnimationView.setAnimation("viewhelper/loading.json");
        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();
        helper.showLayout(layout);
    }

    public void restore() {
        helper.restoreView();
    }
}
