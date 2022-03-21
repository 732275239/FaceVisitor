package com.chuyu.face.utils;

import android.content.Intent;

import com.chuyu.face.base.ApplicationContext;
import com.chuyu.face.ui.FirstActivity;
import com.chuyu.face.ui.MainActivity;
import com.jakewharton.processphoenix.ProcessPhoenix;

/**
 * @author Zoello
 * @description:
 * @date : 2021/6/23 11:59
 */
public class CrashHandler  implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Intent i= new Intent(ApplicationContext.getInstance(), FirstActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //自启动APP（Activity）
        ProcessPhoenix.triggerRebirth(ApplicationContext.getInstance(), i);
    }
}
