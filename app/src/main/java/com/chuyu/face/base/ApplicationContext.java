package com.chuyu.face.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.chuyu.face.bean.MyObjectBox;
import com.chuyu.face.tools.share.SharedPreferencesTools;

import com.chuyu.face.utils.CrashHandler;
import com.whieenz.LogCook;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.OkHttpNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

import io.objectbox.BoxStore;


public class ApplicationContext extends Application {
    private static ApplicationContext instance;
    private SharedPreferencesTools spTools;
    private BoxStore boxStore;

    public SharedPreferencesTools getSpTools() {
        if (spTools == null) {
            spTools = new SharedPreferencesTools(instance);
        }
        return spTools;
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public Context getAppContext(){
        return this.getApplicationContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initNet();
        spTools = getSpTools();
        //数据库
        boxStore = MyObjectBox.builder().androidContext(this).build();
        //日志
//        Log();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }
    private void Log() {
        String logPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/log";
        LogCook.getInstance()
                .setLogPath(logPath)
                .setLogName("chuyu.log")
                .isOpen(true)
                .isSave(true)
                .initialize();
    }
    public BoxStore getBoxStore() {
        return boxStore;
    }

    private void initNet() {
        try{
            // 如果你需要自定义配置：
            NoHttp.initialize(this, new NoHttp.Config()
                    // 设置全局连接超时时间，单位毫秒，默认10s。
                    .setConnectTimeout(30 * 1000)
                    // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                    .setReadTimeout(30 * 1000)
                    // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                    .setCacheStore(
                            new DBCacheStore(this).setEnable(false) // 如果不使用缓存，设置false禁用。
                    )
                    // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                    .setCookieStore(
                            new DBCookieStore(this).setEnable(false) // 如果不维护cookie，设置false禁用。
                    )
                    // 使用OkHttp
                    .setNetworkExecutor(new OkHttpNetworkExecutor())
            );
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
