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
import android.widget.ImageView;
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
import com.chuyu.face.utils.FaceDetectorUtils;
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
 * ????????????
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
    private boolean allow = true; //????????????????????????
    private boolean isLedOn = false;//LED????????????
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
    private ImageView landscape;
    private int current = 11;//??????????????????
    private int showlandscape = 12;//????????????
    private int inOrOut; // 1??? 2???
    private int minScore;
    private SoundPool spPool;
    private int sound;
    private ExecutorService executorService;
    private int hour1;
    private int minute1;
    private int hour2;
    private int minute2;

    public static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(4, 4,
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
        landscape = findViewById(R.id.landscape);
        pass = findViewById(R.id.pass);
        login = findViewById(R.id.login);
        cancel = findViewById(R.id.cancel);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
        adminbt.setOnClickListener(this);
        landscape.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");
        time.setTypeface(typeface);
        PgyCrashManager.register();
//        PgyCrashManager.setIsIgnoreDefaultHander(true);
        showlandscape();
    }

    @Override
    protected void initDatas() {
        initListener();
        //???????????????
        initHardware();
        //?????????????????????
        initconfig();
        //?????????????????????
        initOpenMusic();
        //???????????????
        executorService = newSingleThreadExecutor();
    }

    private void startDetect() {
        if (!faceDetectView.isHasInit()) {
            //????????????view????????????????????????
            faceDetectView.initView();
            faceDetectView.initCamera();
            faceDetectView.getDetectConfig().CameraType = 0;
            faceDetectView.getDetectConfig().EnableFaceDetect = true;
            faceDetectView.getDetectConfig().Simple = 0.2f;//????????????????????????????????????0~1????????????????????????
            faceDetectView.getDetectConfig().MinDetectTime = 300;
            faceDetectView.getDetectConfig().MaxDetectTime = 600;//??????????????????????????????0.8??????????????????????????????
            faceDetectView.getDetectConfig().EnableIdleSleepOption = true;//??????????????????????????????
            faceDetectView.getDetectConfig().IdleSleepOptionJudgeTime = 1000 * 10;//10??????????????????????????????????????????????????????
        }
        faceDetectView.startCameraPreview();
        faceDetectView.setOnstart(b -> {
            Log.e("log", b ? "??????" : "??????");
            if (!b) {
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
                executorService.execute(() -> extractFaces(datas, width, height));
            }
        });
        faceDetectView.setFlashListener(() -> {
            runOnUiThread(() -> showlandscape());
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
                        }, 5000);
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
//          mface = ZKLiveFaceManager.getInstance().getTemplateFromNV21(datas, width, height);
        facepath = convertPicture(datas, width, height);
        byte[] mface = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(mBitmap);
        if (mface != null) {
            String id = ZKLiveFaceManager.getInstance().identify(mface, minScore);
            if (id == null || id.isEmpty()) {
                //??????????????? (??????)
                visitorHandle();
            } else {
                //???????????????
                List<FaceData> faceDatas = BlogDao.getBlogItemBox().query().equal(FaceData_.idno, id).build().find();
                if (faceDatas != null && faceDatas.size() > 0) {
                    FaceData faceData = faceDatas.get(0);
                    OpenDoor(faceData.getName());
                    UploadFile(facepath, 2, faceData);
                    faceDatas.clear();
                } else {
                    failMonitor("????????????");
                }
            }
        } else {
            stopMonitor();
        }
    }

    //??????????????? (??????)
    private void visitorHandle() {
        if (inOrOut == 1) {
            //???
            boolean visIn = (boolean) SharedPreferencesUtils.getParam(this, "visitorIn", false);
            if (visIn) {
                OpenDoor("????????????");
                UploadFile(facepath, 1, null);
            } else {
                failMonitor("?????????????????????");
            }

        } else {
            //???
            boolean visOut = (boolean) SharedPreferencesUtils.getParam(this, "visitorOut", false);
            if (visOut) {
                OpenDoor("????????????");
                UploadFile(facepath, 1, null);
            } else {
                failMonitor("?????????????????????");
            }
        }

    }

    private void OpenDoor(String s) {
        runOnUiThread(() -> {
            completeMonitor(s);
            openDoor();
        });
    }

    private void UploadFile(String facepath, int i, FaceData face) {
        //1 ?????? 2 ??????
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

    //??????
    private void PersonnelInAndOut(FaceData face, String imgpath) {
        // 1??? 2???
        Map<String, Object> param = new HashMap<>();
        param.put("userName", face.getName());
        param.put("userId", face.getIdno());
        param.put("imgPath", imgpath);
        param.put("code", inOrOut == 1 ? "0" : "1");//0 ?????????1 ??????
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

    //??????
    private void VisitorsInAndOut(String imgpath) {
        Map<String, Object> param = new HashMap<>();
        param.put("imgPath", imgpath);
        param.put("code", inOrOut == 1 ? "0" : "1");//0 ?????????1 ??????
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
                        handler.sendMessageDelayed(handler.obtainMessage(current), 15000);
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
            case R.id.landscape:
                showlandscape();
                break;
            case R.id.cancel:
                if (adminLayout.getVisibility() == View.VISIBLE) {
                    handler.removeMessages(current);
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
            case 11://??????????????????
                adminLayout.setVisibility(View.GONE);
                allow = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
                break;
            case 12://????????????
                if (allow) {
                    landscape.setVisibility(View.VISIBLE);
                } else {
                    showlandscape();
                }
                break;

            default:
                break;
        }
    }

    private void showlandscape() {
        landscape.setVisibility(View.GONE);
        handler.removeMessages(showlandscape);
        handler.sendMessageDelayed(handler.obtainMessage(showlandscape), 20000);
    }

    /**
     * ?????????????????????
     */
    private void login() {
        String trim = pass.getText().toString().trim();
        if (trim.isEmpty()) {
            showToast("???????????????", 2);
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
            showToast("????????????", 2);
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

    //????????????
    private void startMonitor() {
        allow = false;
    }

    //????????????
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
                    showToast("?????????????????????", 2);
                }
            } catch (SdkException e) {
                showToast("?????????????????????", 2);
                e.printStackTrace();
            }
        }
    }

    //???????????????
    private void failMonitor(String s) {
        handler.postDelayed(() -> allow = true, 2000);
        showToast(s, 2);
    }

    //??????????????????
    private void stopMonitor() {
        allow = true;
    }

    private void openDoor() {
        try {
            if (zkDevice.setLock1(0)) {
            } else {
                showToast("????????????", 0);
            }
        } catch (SdkException e) {
            e.printStackTrace();
        }
        handler.postDelayed(() -> {
            try {
                if (zkDevice.setLock1(1)) {
                } else {
                    showToast("????????????", 0);
                }
            } catch (SdkException e) {
                e.printStackTrace();
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (faceDetectView != null) {
            handler.postDelayed(() -> startDetect(), 2000);
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

    private String convertPicture(byte[] s, int w, int h) {
        try {
            /**
             * Nv21ToBitmapUtils ???????????????bitmap??? RGBA_8888  ????????????RGB_565??????????????????
             */
            Bitmap bitmap = Nv21ToBitmapUtils.getInstance().nv21ToBitmap(s, w, h);
            //??????90???
            mBitmap = adjustPhotoRotation(bitmap, 90);
            if (mBitmap != null) {
                //????????????????????????????????????bitmap
                mBitmap = FaceDetectorUtils.getInstance().getCutBitmap(mBitmap);
                return FileUtil.saveBitmap(mBitmap, "/face.jpg");
            } else {
                return null;
            }
        } catch (Exception ex) {
        }
        return null;
    }

    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError ex) {
        }
        return null;
    }

    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                    if (Tools.isCurrentInTimeScope(hour1, minute1, hour2, minute2)) {
                        //???????????????????????????-??????????????????
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
                        //????????????
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
