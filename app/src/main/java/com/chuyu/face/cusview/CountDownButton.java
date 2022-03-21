package com.chuyu.face.cusview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Button;

import com.chuyu.face.R;


/**
 * Created by Zoello on 2019/2/13.
 * 倒计时bt
 *
 *
 * 绑定资源销毁
 *  @Override
        protected void onDestroy() {
        super.onDestroy();
        if (!hqyzm.isFinish()) {
        hqyzm.cancel();
        }
    }

//点击后判断
    if (hqyzm.isFinish()) {
    //发送验证码请求成功后调用
    hqyzm.start();
    }
 */

@SuppressLint("AppCompatCustomView")
public class CountDownButton extends Button {
    //总时长
    private long millisinfuture;
    //间隔时长
    private long countdowninterva;
    //默认背景颜色
    private int normalColor;
    //倒计时 背景颜色
    private int countDownColor;
    //是否结束
    private boolean isFinish;
    //定时器
    private CountDownTimer countDownTimer;

    public CountDownButton(Context context) {
        this(context, null);
    }

    public CountDownButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceAsColor")
    public CountDownButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownButton, defStyleAttr, 0);
        //设置默认时长
        millisinfuture = (long) typedArray.getInt(R.styleable.CountDownButton_millisinfuture, 60000);
        //设置默认间隔时长
        countdowninterva = (long) typedArray.getInt(R.styleable.CountDownButton_countdowninterva, 1000);
        //设置默认背景色
        normalColor = typedArray.getColor(R.styleable.CountDownButton_normalColor, android.R.color.holo_blue_light);
        //设置默认倒计时背景色
        countDownColor = typedArray.getColor(R.styleable.CountDownButton_countDownColor, android.R.color.darker_gray);
        typedArray.recycle();
        //默认为已结束状态
        isFinish = true;
        //字体居中
        setGravity(Gravity.CENTER);
        //默认文字和背景色
        normalBackground();
        //设置定时器
        countDownTimer = new CountDownTimer(millisinfuture, countdowninterva) {
            @Override
            public void onTick(long millisUntilFinished) {
                //未结束
                isFinish = false;
                setText((Math.round((double) millisUntilFinished / 1000) - 1) + "秒");
                setTextColor(countDownColor);
            }

            @Override
            public void onFinish() {
                //结束
                isFinish = true;
                normalBackground();
            }
        };
    }

    private void normalBackground() {
        setText("获取验证码");
        setTextColor(normalColor);
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void cancel() {
        countDownTimer.cancel();
    }

    public void start() {
        countDownTimer.start();
    }
}


