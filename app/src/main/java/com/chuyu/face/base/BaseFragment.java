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

package com.chuyu.face.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chuyu.face.cusview.LoadingDailog;
import com.chuyu.face.cusview.loading.VaryViewHelperController;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.ToastUtil;

import java.lang.reflect.Field;

import de.greenrobot.event.EventBus;
import immortalz.me.library.TransitionsHeleper;

/**
 *名称：Fragment基类（普通的Fragment）
 *详细说明：没有生命周期判断，在普通的Fragment中使用
 */
public abstract class BaseFragment extends Fragment implements OnClickListener{

    /**
     * context
     */
    protected Context mContext = null;

    protected LayoutInflater inflater;

    protected View rootView;

    /**
     * loading view 控制器
     */
    protected VaryViewHelperController mVaryViewHelperController = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getBundleExtras(getArguments());
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getContentViewLayoutID() != 0) {
            rootView=inflater.inflate(getContentViewLayoutID(), null);
        } else {
            rootView = super.onCreateView(inflater, container, savedInstanceState);
        }
        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
        this.inflater=inflater;
        return rootView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        initView();
        initDatas();
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().register(this);
        }
    }


    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            eventBusResult(eventCenter);
        }
    }

    /**
     * 是否在这里注册EventBus
     *
     * @return
     */
    protected abstract boolean isRegisterEventBusHere();

    /**
     * 此方法是使用EventBus时，接受数据的方法
     *
     * @param eventCenter
     */
    protected abstract void eventBusResult(EventCenter eventCenter);
    /**
     * Activity的oncreate执行完成后的回调
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    /**
     * bind layout resource file
     *
     * 绑定布局的资源文件
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * get loading target view
     * 得到loading目标视图
     */
    protected abstract View getLoadingTargetView();
    /**
     * 获取 bundle 中的数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

    /**
     * init all views and add events
     * 初始化所有的组件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initDatas();




    /**
     * 重置Loading，即隐藏Loading
     */
    protected void restoreLoading() {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }
        mVaryViewHelperController.restore();
    }


    /**
     * toggle show loading
     * 显示正在加载布局
     *
     * @param toggle
     */
    protected void toggleShowLoading(boolean toggle, String msg) {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading(msg);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show loading
     * 显示正在加载布局
     *
     * @param toggle
     */
    protected void toggleShowLoading(boolean toggle) {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }

        if (toggle) {
            mVaryViewHelperController.showLoading("加载中...");
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show empty
     * 没有数据时显示空界面提示
     *
     * @param toggle
     */
    protected void toggleShowEmpty(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }

        if (toggle) {
            mVaryViewHelperController.showEmpty(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show error
     * 界面错误时显示错误提示界面
     *
     * @param toggle
     */
    protected void toggleShowError(boolean toggle, String msg, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }

        if (toggle) {
            mVaryViewHelperController.showError(msg, onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    /**
     * toggle show network error
     * 当网络异常时，显示网络异常界面提示
     *
     * @param toggle
     */
    protected void toggleNetworkError(boolean toggle, View.OnClickListener onClickListener) {
        if (null == mVaryViewHelperController) {
            /**
             * {@link #getLoadingTargetView()}
             */
            throw new IllegalArgumentException("你必须要在getLoadingTargetView()方法中返回一个正确的View");
        }

        if (toggle) {
            mVaryViewHelperController.showNetworkError(onClickListener);
        } else {
            mVaryViewHelperController.restore();
        }
    }

    public LoadingDailog getLoading() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(getActivity())
                .setMessage("加载中...")
                .setCancelable(false)
                .setCancelOutside(false);
        return loadBuilder.create();
    }

    /**
     * get the support fragment manager
     *
     * @return
     */
    protected FragmentManager getSupportFragmentManager() {
        return getActivity().getFragmentManager();
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) rootView.findViewById(id);
    }

    protected void showToast(String text) {
        ToastUtil.showTips(getActivity(),text);
    }

    /**
     * 跳转到另外一个Activity 滑动效果
     * @param cls
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startActivitySlide(Class<?> cls) {
        startActivity(new Intent(getActivity(), cls),
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    /**
     * 跳转到另外一个Activity,并关闭当前页面
     * @param cls
     */
    protected void startActivityNowThenKill(Class<?> cls) {
        startActivity(new Intent(getActivity(), cls));
        getActivity().finish();
    }

    /**
     * 跳转到另外一个Activity 揭露效果
     *
     * @param cls
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startActivityJieLu(Class<?> cls, View view) {
        TransitionsHeleper.startActivity(getActivity(), cls, view);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onClick(View v) {

    }


}
