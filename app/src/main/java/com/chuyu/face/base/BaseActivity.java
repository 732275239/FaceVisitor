package com.chuyu.face.base;

import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.chuyu.face.cusview.LoadingDailog;
import com.chuyu.face.cusview.loading.VaryViewHelperController;
import com.chuyu.face.tools.EventBus.EventCenter;

import java.lang.ref.WeakReference;

import de.greenrobot.event.EventBus;
import es.dmoral.toasty.Toasty;
import immortalz.me.library.TransitionsHeleper;


/**
 * @author Zoello
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    protected Context mContext = null;

    protected FragmentManager fragmentManager;

    protected Handler handler;

    protected VaryViewHelperController mVaryViewHelperController = null;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //让布局向上移来显示软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            getBundleExtras(extras);
        }
        BaseAppManager.getInstance().addActivity(this);
        mContext = this;
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("你的资源文件Id不正确");
        }
        handler = new MyHandler(BaseActivity.this);
        fragmentManager = getFragmentManager();
        initView();
        initDatas();
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        Toasty.Config.getInstance().setTextSize(30).allowQueue(false).apply();
    }

    /**
     * 在这个方法中初始化所有的组件
     */
    protected abstract void initView();

    /**
     * 在这个方法中初始化数据
     */
    protected abstract void initDatas();

    /**
     * 绑定布局的资源文件
     *
     * @return 资源文件Id
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 此方法的意思是传递进一个布局，这个布局是用来替代（展示成）Loading布局的
     * 如果返回为空的话，则不能使用Loading布局
     */
    protected abstract View getLoadingTargetView();

    //dispatchTouchEvent + isShouldHideInput 实现收回键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    /**
     * Handler回调方法
     * 防止内存泄露
     * @param msg
     */
    protected void handleMessage(Message msg) {

    }

    protected class MyHandler extends Handler {
        WeakReference<BaseActivity> context;

        public MyHandler(BaseActivity activity) {
            context = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity = context.get();

            if (activity == null) {
                return;
            }
            activity.handleMessage(msg);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BaseAppManager.getInstance().removeActivity(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        TransitionsHeleper.unbind(this);
        super.onDestroy();
        if (isRegisterEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
    }

    public void showToast(String s, int status) {
        if (status == 1) {
            Toasty.success(mContext, s, Toast.LENGTH_SHORT, false).show();
        } else {
            Toasty.error(mContext, s, Toast.LENGTH_SHORT, false).show();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (null != getLoadingTargetView()) {
            mVaryViewHelperController = new VaryViewHelperController(getLoadingTargetView());
        }
    }

    //全局loading弹窗
    public LoadingDailog getLoading() {
        LoadingDailog.Builder loadBuilder = new LoadingDailog.Builder(this)
                .setMessage("请稍等...")
                .setCancelable(false)
                .setCancelOutside(false);
        LoadingDailog loadingDailog = loadBuilder.create();

        return loadingDailog;
    }

    @Override
    public void finish() {
        super.finish();
        BaseAppManager.getInstance().removeActivity(this);
    }

    /**
     * 跳转到另外一个Activity 无效果
     *
     * @param cls
     */

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(BaseActivity.this, cls));
    }

    /**
     * 跳转到另外一个Activity 滑动效果
     *
     * @param cls
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startActivitySlide(Class<?> cls) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(BaseActivity.this, cls),
                    ActivityOptions.makeSceneTransitionAnimation(BaseActivity.this).toBundle());
        } else {
            startActivity(cls);
        }

    }

    /**
     * 跳转到另外一个Activity 揭露效果
     *
     * @param cls
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startActivityJieLu(Class<?> cls, View view) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionsHeleper.startActivity(this, cls, view);
        } else {
            startActivity(cls);
        }

    }

    /**
     * 跳转到另外一个Activity,并关闭当前页面
     *
     * @param intent
     */
    protected void startActivityNowThenKill(Intent intent) {
        startActivity(intent);
        finish();
    }


    /**
     * 获取 bundle 中的数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            }
            return true;
        }
//        return super.dispatchKeyEvent(event);
        return false;
    }

}
