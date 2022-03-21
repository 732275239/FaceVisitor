package com.chuyu.face.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextClock;

import com.bifan.detectlib.FaceDetectView;
import com.chuyu.face.R;
import com.chuyu.face.base.ApplicationContext;
import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.base.URLs;
import com.chuyu.face.bean.BlogDao;
import com.chuyu.face.bean.FaceData;
import com.chuyu.face.bean.FaceData_;
import com.chuyu.face.bean.imgfile;
import com.chuyu.face.http.NetResult;
import com.chuyu.face.http.OkHttp;
import com.chuyu.face.http.OkHttpCallBack;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.Tools;
import com.chuyu.face.tools.ZKLiveFaceManager;
import com.chuyu.face.tools.share.SharedPreferencesUtils;
import com.chuyu.face.utils.FileUtil;
import com.chuyu.face.utils.GsonUtil;
import com.chuyu.face.utils.Nv21ToBitmapUtils;
import com.chuyu.face.utils.SoftInputUtils;
import com.google.gson.reflect.TypeToken;
import com.pgyersdk.crash.PgyCrashManager;
import com.zkteco.android.constant.SdkException;
import com.zkteco.android.device.Device;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * @author Zoello
 * 追踪人脸
 */
public class MainActivity extends BaseActivity {
    Device zkDevice;
    private volatile boolean isOpenDevice;
    private final BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (com.zkteco.android.constant.Const.ACTION_USB_PERMISSION.equals(intent.getAction())) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    try {
                        if (zkDevice.openDevice()) {
                            isOpenDevice = true;
                        }
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    private View imgbg;
    private boolean allow = true; //能否进行人脸识别
    private boolean isLedOn = false;//LED状态开？
    private IntentFilter timeFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private RelativeLayout adminLayout, facelayout;
    private FaceDetectView faceDetectView;
    private EditText pass;
    private Button cancel, login;
    private CircleImageView faceimg;
    private String facepath;
    private Bitmap mBitmap;
    private TextClock time;
    private View adminbt;
    private int current = 11;
    private int inOrOut; // 1进 2出
    private int minScore;
    private SoundPool spPool;
    private int sound;
    private ExecutorService executorService;
    private int hour1;
    private int minute1;
    private int hour2;
    private int minute2;

    public static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(2, 3,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Override
    protected void initView() {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        time = (TextClock) findViewById(R.id.time);
        faceimg = findViewById(R.id.faceimg);
        imgbg = (View) findViewById(R.id.imgbg);
        facelayout = findViewById(R.id.facelayout);
        adminbt = (View) findViewById(R.id.adminbt);
        adminLayout = findViewById(R.id.adminLayout);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        cancel = findViewById(R.id.cancel);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        adminbt.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");
        time.setTypeface(typeface);
        PgyCrashManager.register();
//        PgyCrashManager.setIsIgnoreDefaultHander(true);
    }

    @Override
    protected void initDatas() {
        initListener();
        //初始化硬件
        initHardware();
        //初始化本地配置
        initconfig();
        //初始化开门音效
        initOpenMusic();
        //创建线程池
        executorService = newSingleThreadExecutor();
    }

    private void startDetect() {
        if (!faceDetectView.isHasInit()) {
            //必须是在view可见后进行初始化
            faceDetectView.initView();
            faceDetectView.initCamera();
            faceDetectView.getDetectConfig().CameraType = 0;
            faceDetectView.getDetectConfig().EnableFaceDetect = true;
            faceDetectView.getDetectConfig().Simple = 0.3f;//图片检测时的压缩取样率，0~1，越小检测越流畅
            faceDetectView.getDetectConfig().MinDetectTime = 200;
            faceDetectView.getDetectConfig().MaxDetectTime = 800;//进入智能休眠检测，以0.8秒一次的这个速度检测
            faceDetectView.getDetectConfig().EnableIdleSleepOption = true;//启用智能休眠检测机制
            faceDetectView.getDetectConfig().IdleSleepOptionJudgeTime = 1000 * 10;//10秒内没有检测到人脸，进入智能休眠检测
        }
        faceDetectView.startCameraPreview();
        faceDetectView.setOnstart(b -> {
            Log.e("log", b?"成功":"失败");
            if (!b){
                finish();
            }
        });
        Log.e("log", "startDetect");
    }

    private void initListener() {
        faceDetectView = findViewById(R.id.faceDetectView);
        faceDetectView.setFramePreViewListener((datas, width, height) -> {
            if (datas != null && allow) {
                startMonitor();
                extractFaces(datas, width, height);
            }
        });
        faceDetectView.setFlashListener(() -> {
            if (!isLedOn) {
                try {
                    if (zkDevice.setLED(1)) {
                        isLedOn = true;
                        handler.postDelayed(() -> {
                            try {
                                if (zkDevice.setLED(0)) {
                                    isLedOn = false;
                                }
                            } catch (SdkException e) {
                                e.printStackTrace();
                            }
                        },5000);
                    }
                } catch (SdkException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initOpenMusic() {
        spPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound = spPool.load(this, R.raw.open_ok, 1);
    }

    private void extractFaces(byte[] datas, int width, int height) {
        runOnUiThread(() -> {
//          mface = ZKLiveFaceManager.getInstance().getTemplateFromNV21(datas, width, height);
            facepath = convertPicture(datas, width, height);
            byte[]  mface = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(mBitmap);
            if (mface != null) {
                String id = ZKLiveFaceManager.getInstance().identify(mface, minScore);
                if (id == null || id.isEmpty()) {
                    //人脸未登记 (访客)
                    UploadFile(facepath, 1, null);
//                        if (inOrOut == 1) {
//                            failMonitor("访客请扫二维码");
//                            showToast("访客请扫二维码", 0);
//                        } else {
                    failMonitor("请联系门岗开门");
//                        }
                } else {
                    //人脸已登记
                    List<FaceData> faceDatas = BlogDao.getBlogItemBox().query().equal(FaceData_.idno, id).build().find();
                    if (faceDatas != null && faceDatas.size() > 0) {
                        FaceData faceData = faceDatas.get(0);
                        OpenDoor(faceData.getName());
                        UploadFile(facepath, 2, faceData);
                        faceDatas.clear();
                    } else {
                        failMonitor("人脸失效");
                    }
                }
            } else {
                stopMonitor();
            }
        });
    }

    private void OpenDoor(String s) {
        completeMonitor(s);
        openDoor();
    }

    private void UploadFile(String facepath, int i, FaceData face) {
        //1 访客 2 内部
        File file = new File(facepath);
        OkHttp.postFile(this, URLs.uploadFile, file, new OkHttpCallBack<imgfile>(new TypeToken<imgfile>() {
        }) {
            @Override
            public void onError() {
                super.onError();
            }

            @Override
            public void onFailure(String eCode, String eMsg) {
                super.onFailure(eCode, eMsg);
            }

            @Override
            public void onSuccess(imgfile rsData, String eCode, String eMsg) {
                super.onSuccess(rsData, eCode, eMsg);
                if (i == 1) {
                    VisitorsInAndOut(rsData.getPath());
                } else {
                    PersonnelInAndOut(face, rsData.getPath());
                }
            }
        });
    }

    //内部
    private void PersonnelInAndOut(FaceData face, String imgpath) {
        // 1进 2出
        Map<String, Object> param = new HashMap<>();
        param.put("userName", face.getName());
        param.put("userId", face.getIdno());
        param.put("imgPath", imgpath);
        param.put("code", inOrOut == 1 ? "0" : "1");//0 进入，1 外出
        String params = GsonUtil.ToGson(param);
        OkHttp.postSinge(this, true, URLs.personEntrance, params,
                new OkHttpCallBack<NetResult>(new TypeToken<NetResult>() {
                }) {
                    @Override
                    public void onError() {
                        super.onError();
                    }

                    @Override
                    public void onFailure(String eCode, String eMsg) {
                        super.onFailure(eCode, eMsg);
                    }

                    @Override
                    public void onSuccess(NetResult rsData, String eCode, String eMsg) {
                        super.onSuccess(rsData, eCode, eMsg);
                    }
                });
    }

    //访客
    private void VisitorsInAndOut(String imgpath) {
        Map<String, Object> param = new HashMap<>();
        param.put("imgPath", imgpath);
        param.put("code", inOrOut == 1 ? "0" : "1");//0 进入，1 外出
        String params = GsonUtil.ToGson(param);
        OkHttp.postSinge(this, true, URLs.register, params,
                new OkHttpCallBack<NetResult>(new TypeToken<NetResult>() {
                }) {
                    @Override
                    public void onError() {
                        super.onError();
                    }

                    @Override
                    public void onFailure(String eCode, String eMsg) {
                        super.onFailure(eCode, eMsg);
                    }

                    @Override
                    public void onSuccess(NetResult rsData, String eCode, String eMsg) {
                        super.onSuccess(rsData, eCode, eMsg);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.adminbt:
                if (allow) {
                    if (adminLayout.getVisibility() == View.GONE) {
                        adminLayout.setVisibility(View.VISIBLE);
                        allow = false;
                        handler.removeMessages(current);
                        Message msg = handler.obtainMessage(current);
                        handler.sendMessageDelayed(msg, 15000);
                    }
                    return;
                }
                if (adminLayout.getVisibility() == View.VISIBLE) {
                    adminLayout.setVisibility(View.GONE);
                    allow = true;
                }

                break;
            case R.id.login:
                login();
                break;
            case R.id.cancel:
                if (adminLayout.getVisibility() == View.VISIBLE) {
                    adminLayout.setVisibility(View.GONE);
                    allow = true;
                    pass.setText("");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case 11://10秒无操作，自动隐藏
                adminLayout.setVisibility(View.GONE);
                allow = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
                break;
            default:
                break;
        }
    }

    /**
     * 登录管理员页面
     */
    private void login() {
        String trim = pass.getText().toString().trim();
        if (trim.isEmpty()) {
            showToast("请输入密码", 2);
            return;
        }
        String password = (String) SharedPreferencesUtils.getParam(mContext, "password", "123456");
        if (trim.equals(password)) {
            pass.setText("");
            SoftInputUtils.hideSoftInput(this);
            startActivity(AdminActivity.class);
            adminLayout.setVisibility(View.GONE);
            allow = true;
            finish();
        } else {
            showToast("密码错误", 2);
            return;
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected boolean isRegisterEventBusHere() {
        return false;
    }

    @Override
    protected void eventBusResult(EventCenter eventCenter) {

    }

    //开始识别
    private void startMonitor() {
        allow = false;
    }

    //完成识别
    private void completeMonitor(String s) {
        if (facepath != null) {
            faceimg.setImageBitmap(mBitmap);
            facelayout.setVisibility(View.VISIBLE);
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1000);
            imgbg.startAnimation(rotate);
        }
        handler.postDelayed(() -> {
            facelayout.setVisibility(View.GONE);
            if (!mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
            allow = true;
        }, 1000);

        executorService.execute(() -> spPool.play(sound, 1, 1, 1, 0, 1));
        showToast(s, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isOpenDevice) {
            try {
                if (zkDevice.openDevice()) {
                    isOpenDevice = true;
                } else {
                    showToast("硬件初始化失败", 2);
                }
            } catch (SdkException e) {
                showToast("硬件初始化失败", 2);
                e.printStackTrace();
            }
        }
    }

    //识别不通过
    private void failMonitor(String s) {
        handler.postDelayed(() -> allow = true, 2000);
        showToast(s, 2);
    }

    //继续监听人脸
    private void stopMonitor() {
        allow = true;
    }

    private void openDoor() {
        try {
            if (zkDevice.setLock1(0)) {
            } else {
                showToast("开门故障", 0);
            }
        } catch (SdkException e) {
            e.printStackTrace();
        }
        handler.postDelayed(() -> {
            try {
                if (zkDevice.setLock1(1)) {
                } else {
                    showToast("开门故障", 0);
                }
            } catch (SdkException e) {
                e.printStackTrace();
            }
        }, 2000);
    }

    private String convertPicture(byte[] s, int w, int h) {
        try {
            /**
             * Nv21ToBitmapUtils 转换处理的bitmap是 RGBA_8888  需要转为RGB_565进行人脸裁剪
             */
            Bitmap bitmap = Nv21ToBitmapUtils.getInstance().nv21ToBitmap(s, w, h);
            //旋转90度
            mBitmap = adjustPhotoRotation(bitmap, 90);
            if (mBitmap != null) {
                //先保存为图片文件，再转为bitmap
//                mBitmap = FaceDetectorUtils.getInstance().getCutBitmap(mBitmap);
                return FileUtil.saveBitmap(mBitmap, "/face.jpg");
            } else {
                return null;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
     * 旋转bitmap角度
     *
     * @param bm
     * @param orientationDegree
     * @return
     */
    Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (faceDetectView != null) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startDetect();
                }
            }, 2000);
            Log.e("abc", "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (faceDetectView != null) {
            endDetect();
            Log.e("abc", "onPause");
        }
    }

    public void endDetect() {
        Log.e("abc", "endDetect");
        faceDetectView.stopCameraPreview();
        faceDetectView.getFaceRectView().clearBorder();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (faceDetectView != null) {
            faceDetectView.release();
            Log.e("abc", "Stop");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("abc", "onDestroy");
        if (spPool != null) {
            spPool.stop(sound);
            spPool.release();
        }
        try {
            if (zkDevice.setLED(0)) {
            }
        } catch (SdkException e) {
            e.printStackTrace();
        }
        unregisterReceiver(usbBroadcastReceiver);
        unregisterReceiver(timeChangeReceiver);
        handler.removeMessages(current);
        zkDevice.closeDevice();
    }

    private void initHardware() {
        isOpenDevice = false;
        zkDevice = new Device(getApplicationContext());
        zkDevice.debugeInformation(true);

        IntentFilter intentFilter = new IntentFilter(com.zkteco.android.constant.Const.ACTION_USB_PERMISSION);
        registerReceiver(usbBroadcastReceiver, intentFilter);

        timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, timeFilter);
    }

    private void initconfig() {
        inOrOut = (int) SharedPreferencesUtils.getParam(mContext, "inOrOut", 0);
        minScore = (int) SharedPreferencesUtils.getParam(this, "minScore", 76);
        hour1 = (int) SharedPreferencesUtils.getParam(mContext, "hour1", 18);
        minute1 = (int) SharedPreferencesUtils.getParam(mContext, "minute1", 0);
        hour2 = (int) SharedPreferencesUtils.getParam(mContext, "hour2", 8);
        minute2 = (int) SharedPreferencesUtils.getParam(mContext, "minute2", 0);
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    if (Tools.isCurrentInTimeScope(hour1, minute1, hour2, minute2)) {
                        //补光灯在开启范围内-需要检查开始
                        if (!isLedOn) {
                            try {
                                if (zkDevice.setLED(1)) {
                                    isLedOn = true;
                                }
                            } catch (SdkException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //需要关灯
                        if (isLedOn) {
                            try {
                                if (zkDevice.setLED(0)) {
                                    isLedOn = false;
                                }
                            } catch (SdkException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
