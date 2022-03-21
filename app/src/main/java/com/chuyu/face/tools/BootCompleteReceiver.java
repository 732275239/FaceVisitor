package com.chuyu.face.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chuyu.face.ui.FirstActivity;
import com.chuyu.face.ui.MainActivity;


public class BootCompleteReceiver extends BroadcastReceiver {

    public BootCompleteReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //此处及是重启的之后，打开我们app的方法
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent i= new Intent(context, FirstActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 非常重要，如果缺少的话，程序将在启动时报错
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //自启动APP（Activity）
            context.startActivity(i);
            //自启动服务（Service）
            //context.startService(intent);
        }
    }
}
