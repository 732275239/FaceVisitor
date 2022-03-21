package com.chuyu.face.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuyu.face.R;
import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.share.SharedPreferencesUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author Zoello
 * @description: 补光灯开关时间
 * @date : 2021/5/24 10:18
 */
public class LightTimeActivity extends BaseActivity {
    private ImageView topBarLeftImg;
    private TextView topBarTitleTv;
    private LinearLayout begin;
    private LinearLayout end;
    private TextView ks;
    private TextView js;

    Calendar calendar = Calendar.getInstance(Locale.CHINA);

    @Override
    protected void initView() {

        topBarLeftImg = (ImageView) findViewById(R.id.top_bar_leftImg);
        topBarTitleTv = (TextView) findViewById(R.id.top_bar_titleTv);
        begin = (LinearLayout) findViewById(R.id.begin);
        end = (LinearLayout) findViewById(R.id.end);
        topBarTitleTv.setText("补光时间设置");
        ks = (TextView) findViewById(R.id.ks);
        js = (TextView) findViewById(R.id.js);
        topBarLeftImg.setOnClickListener(this);
        begin.setOnClickListener(this);
        end.setOnClickListener(this);
    }

    @Override
    protected void initDatas() {
        int hour1 = (int) SharedPreferencesUtils.getParam(mContext, "hour1", 18);
        int minute1 = (int) SharedPreferencesUtils.getParam(mContext, "minute1", 0);
        int hour2 = (int) SharedPreferencesUtils.getParam(mContext, "hour2", 8);
        int minute2 = (int) SharedPreferencesUtils.getParam(mContext, "minute2", 0);
        ks.setText(hour1 + "：" + minute1);
        js.setText(hour2 + "：" + minute2);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_lighttime;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBusHere() {
        return false;
    }

    @Override
    protected void eventBusResult(EventCenter eventCenter) {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_bar_leftImg:
                finish();
                break;
            case R.id.begin:
                TimePickerDialog dialog = new TimePickerDialog(this, 4,
                        // 绑定监听器
                        (view, hourOfDay, minute) -> {
                            SharedPreferencesUtils.setParam(mContext, "hour1", hourOfDay);
                            SharedPreferencesUtils.setParam(mContext, "minute1", minute);
                            ks.setText(hourOfDay + "：" + minute);
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true);
                dialog.show();
                //放在show()之后，不然有些属性是没有效果的，比如height和width
                Window dialogWindow = dialog.getWindow();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
                WindowManager.LayoutParams p = dialogWindow.getAttributes();
                // 设置宽度
                p.width = (int) (d.getWidth() * 0.9);
                // 宽度设置为屏幕的0.95
                p.gravity = Gravity.CENTER;//设置位置
                //p.alpha = 0.8f;//设置透明度
                dialogWindow.setAttributes(p);
                break;
            case R.id.end:
                TimePickerDialog dialog2 = new TimePickerDialog(this, 4,
                        // 绑定监听器
                        (view, hourOfDay, minute) -> {
                            SharedPreferencesUtils.setParam(mContext, "hour2", hourOfDay);
                            SharedPreferencesUtils.setParam(mContext, "minute2", minute);
                            js.setText(hourOfDay + "：" + minute);
                        }
                        // 设置初始时间
                        , calendar.get(Calendar.HOUR_OF_DAY)
                        , calendar.get(Calendar.MINUTE)
                        // true表示采用24小时制
                        , true);
                dialog2.show();
                //放在show()之后，不然有些属性是没有效果的，比如height和width
                Window dialogWindow2 = dialog2.getWindow();
                WindowManager m2 = getWindowManager();
                Display d2 = m2.getDefaultDisplay(); // 获取屏幕宽、高
                WindowManager.LayoutParams p2 = dialogWindow2.getAttributes();
                // 设置宽度
                p2.width = (int) (d2.getWidth() * 0.9);
                // 宽度设置为屏幕的0.95
                p2.gravity = Gravity.CENTER;//设置位置
                //p.alpha = 0.8f;//设置透明度
                dialogWindow2.setAttributes(p2);
                break;
            default:
                break;
        }
    }
}
