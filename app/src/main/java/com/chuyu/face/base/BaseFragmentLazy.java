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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chuyu.face.anim.PropertyAnimation;
import com.chuyu.face.cusview.loading.VaryViewHelperController;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.netstatus.NetChangeObserver;
import com.chuyu.face.tools.netstatus.NetStateReceiver;
import com.chuyu.face.tools.netstatus.NetUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.greenrobot.event.EventBus;

/**
 *名称：Fragment基类（懒加载，用于ViewPager）
 *创建人：xw
 *创建时间：2017/3/20 下午 1:51
 *详细说明：具有懒加载功能
 */
public abstract class BaseFragmentLazy extends Fragment {

    /**
     * Class Log tag
     */
    protected static String TAG_LOG = null;

    /**
     * 屏幕信息
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;

    /**
     * context
     */
    protected Context mContext = null;


    protected PropertyAnimation pa;

    protected Handler handler;

    /**
     * 网络状态回调类
     */
    protected NetChangeObserver mNetChangeObserver = null;

    //第一次Resume
    private boolean isFirstResume = true;
    //第一次可见
    private boolean isFirstVisible = true;
    //第一次不可见
    private boolean isFirstInvisible = true;
    private boolean isPrepared = false;//判断Fragment布局是否加载完毕

    protected View rootView;
    private VaryViewHelperController mVaryViewHelperController = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG_LOG = this.getClass().getSimpleName();
        pa = new PropertyAnimation(getActivity());
        handler = new MyHandler(BaseFragmentLazy.this);
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

        return rootView;
    }
    private Unbinder bind;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bind = ButterKnife.bind(this,view);

        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;

        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetUtils.NetType type) {
                super.onNetConnected(type);
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                onNetworkDisConnected();
            }
        };
        //监听网络变化
        NetStateReceiver.registerObserver(mNetChangeObserver);

        initView();
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().register(this);
        }
    }



    /**
     * Handler回调方法
     *
     * @param msg
     */
    protected void handleMessage(Message msg) {

    }

    private class MyHandler extends Handler {
        WeakReference<BaseFragmentLazy> context;

        public MyHandler(BaseFragmentLazy fragment) {
            context = new WeakReference<BaseFragmentLazy>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseFragmentLazy fragment = context.get();
            if (fragment == null) {
                return;
            }
            fragment.handleMessage(msg);
        }
    }

    /**
     * Activity的oncreate执行完成后的回调
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFirstResume) {
            isFirstResume = false;
            return;
        }
        if (getUserVisibleHint()) {
            onUserVisible();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserInvisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Fragment可见
        if (isVisibleToUser) {
            if (isFirstVisible) {
                isFirstVisible = false;
                initPrepare();
            } else {
                onUserVisible();
            }
        } else {
            if (isFirstInvisible) {
                isFirstInvisible = false;
                onFirstUserInvisible();
                Log.d("TAG","onFirstUserInvisible");
            } else {
                onUserInvisible();
            }
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            onFirstUserVisible();
        } else {
            isPrepared = true;
        }
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
     * when fragment is visible for the first time, here we can do some initialized work or refresh data only once
     *
     * 当Fragment是第一次可见时，我们可以在这里做些初始化工作或者刷新数据，但是只有一次
     */
    protected abstract void onFirstUserVisible();

    /**
     * this method like the fragment's lifecycle method onResume()
     * 这个方法就像Fragment的生命周期方法onResume()
     */
    protected abstract void onUserVisible();

    /**
     * when fragment is invisible for the first time
     * // here we do not recommend do something
     * 当Fragment第一次不可见时
     */
    protected abstract void onFirstUserInvisible() ;

    /**
     * this method like the fragment's lifecycle method onPause()
     * 这个方法就像Fragment的生命周期方法onPause()
     */
    protected abstract void onUserInvisible();

    /**
     * 是否在这里注册EventBus
     *
     * @return
     */
    protected abstract boolean isRegisterEventBusHere();


    /**
     * 此方法是使用EventBus时，接受数据的方法
     * @param eventCenter
     */
    protected abstract void eventBusResult(EventCenter eventCenter);


    /**
     * 网络已连接回调
     */
    protected abstract void onNetworkConnected(NetUtils.NetType type);

    /**
     * 网络断开连接回调
     */
    protected abstract void onNetworkDisConnected();




    /**
     * get the support fragment manager
     *
     * @return
     */
    protected FragmentManager getSupportFragmentManager() {
        return getActivity().getSupportFragmentManager();
    }






    /**
     * 跳转到另外一个Activity
     * @param cls
     */
    protected void startActivityNow(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivityNow(intent);
    }

    /**
     * 跳转到另外一个Activity
     * @param intent
     */
    protected void startActivityNow(Intent intent) {
        startActivity(intent);
    }

    /**
     * 跳转到另外一个Activity,并关闭当前页面
     * @param cls
     */
    protected void startActivityNowThenKill(Class<?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        startActivityNowThenKill(intent);
    }

    /**
     * 跳转到另外一个Activity,并关闭当前页面
     * @param intent
     */
    protected void startActivityNowThenKill(Intent intent) {
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * startActivityForResult
     * @param cls
     * @param requestCode
     */
    protected void startActivityForResultNow(Class<?> cls, int requestCode) {
        Intent intent = new Intent(getActivity(), cls);
        startActivityForResultNow(intent,requestCode);
    }
    /**
     * startActivityForResult
     * @param intent
     */
    protected void startActivityForResultNow(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    public void onEventMainThread(EventCenter eventCenter) {
        if (null != eventCenter) {
            eventBusResult(eventCenter);
        }
    }



    /**
     * 重置Loading，即隐藏Loading
     */
    protected void restoreLoading(){
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
        bind.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
    }


}
