package com.chuyu.face.ui;

import android.Manifest;
import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chuyu.face.R;
import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.base.URLs;
import com.chuyu.face.bean.BlogDao;
import com.chuyu.face.bean.FaceData;
import com.chuyu.face.bean.faces;
import com.chuyu.face.cusview.LoadingDailog;
import com.chuyu.face.dialog.SetUrlDialog;
import com.chuyu.face.http.OkHttp;
import com.chuyu.face.http.OkHttpCallBack;
import com.chuyu.face.tools.Base64_Utils;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.PermissionPageUtils;
import com.chuyu.face.tools.ZKLiveFaceManager;
import com.chuyu.face.tools.share.SharedPreferencesUtils;
import com.chuyu.face.utils.SoftInputUtils;
import com.google.gson.reflect.TypeToken;
import com.zkteco.android.biometric.liveface56.ZKLiveFaceService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import me.wangyuwei.particleview.ParticleView;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;


public class FirstActivity extends BaseActivity {

    private ParticleView partcle;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_first;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void initView() {
        sendBroadcast(new Intent("HIDE_NAVIGATION"));
        sendBroadcast(new Intent("HIDE_STATUS"));
//        sendBroadcast(new Intent("SHOW_NAVIGATION"));
//        sendBroadcast(new Intent("SHOW_STATUS"));
        partcle = (ParticleView) findViewById(R.id.partcle);
        partcle.startAnim();
        partcle.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
            @Override
            public void onAnimationEnd() {
                permisson();
            }
        });
    }

    private void B() {
        String url = (String) SharedPreferencesUtils.getParam(mContext, "url", "");
        if (url.isEmpty()) {
            SetUrlDialog urlDialog = new SetUrlDialog();
            urlDialog.show(FirstActivity.this);
            urlDialog.setYesOnclickListener(new SetUrlDialog.onYesOnclickListener() {
                @Override
                public void onYesClick(String s, int inOrOut) {
                    URLs.HOST_URL = s;
                    SharedPreferencesUtils.setParam(mContext, "url", s);
                    // 1??? 2???
                    SharedPreferencesUtils.setParam(mContext, "inOrOut", inOrOut);
                    A();
                }
            });
        } else {
            URLs.HOST_URL = url;
            A();
        }

    }

    private void A() {
        int retVal = 0;
        //?????????????????????????????????
        if (!ZKLiveFaceService.isAuthorized() && !ZKLiveFaceService.getChipAuthStatus()) {
            String strHwid = "";
            byte[] hwid = new byte[256];
            int retLen[] = new int[1];
            retLen[0] = 256;
            retVal = ZKLiveFaceService.getHardwareId(hwid, retLen);
            if (0 != retVal) {
                Toasty.error(mContext, "???????????????", Toast.LENGTH_SHORT, false).show();
                return;
            }
            strHwid = new String(hwid, 0, retLen[0]);
            String fileName = "/sdcard/" + strHwid + ".lic";
            retVal = ZKLiveFaceService.setParameter(0, 1011, fileName.getBytes(), fileName.length());
            if (0 != retVal) {
                Toasty.error(mContext, "???????????????", Toast.LENGTH_SHORT, false).show();
            }
        }

        long[] context = new long[1];
        retVal = ZKLiveFaceService.init(context);
        if (0 != retVal) {
            Toasty.error(mContext, "???????????????", Toast.LENGTH_SHORT, false).show();
            return;
        }
        ZKLiveFaceService.terminate(context[0]);
        if (ZKLiveFaceManager.getInstance().isAuthorized()) {
            if (ZKLiveFaceManager.getInstance().setParameterAndInit("")) {
                List<FaceData> all = BlogDao.getAll();
                if (all.size() == 0) {
                    getHumanFace();
                } else {
                    for (FaceData face : all) {
                        ZKLiveFaceManager.getInstance().dbAdd(face.getIdno(), face.getFace());
                    }
                   Toasty.success(mContext, "?????????" + all.size() + "???????????????", Toast.LENGTH_SHORT, false).show();
                    startActivity(MainActivity.class);
                    finish();
                }
                all.clear();
            }
        } else {
            Toasty.error(mContext, "???????????????", Toast.LENGTH_SHORT, false).show();
        }
    }

    private void getHumanFace() {
        OkHttp.postArray(this, true, URLs.listUserFace, "",
                new OkHttpCallBack<ArrayList<faces>>(new TypeToken<ArrayList<faces>>() {
                }) {
                    @Override
                    public void onError() {
                        super.onError();
                        Toasty.error(mContext, "????????????", Toast.LENGTH_SHORT, false).show();
                        sendBroadcast(new Intent("SHOW_NAVIGATION"));
                        sendBroadcast(new Intent("SHOW_STATUS"));
                    }

                    @Override
                    public void onFailure(String eCode, String eMsg) {
                        super.onFailure(eCode, eMsg);
                        Toasty.error(mContext, "????????????", Toast.LENGTH_SHORT, false).show();
                        sendBroadcast(new Intent("SHOW_NAVIGATION"));
                        sendBroadcast(new Intent("SHOW_STATUS"));
                    }

                    @Override
                    public void onSuccess(ArrayList<faces> rsData, String eCode, String eMsg) {
                        super.onSuccess(rsData, eCode, eMsg);
                        if (rsData != null && rsData.size() > 0) {
                            BlogDao.delete();
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    for (faces face : rsData) {
                                        template = null;
                                        template = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(Base64_Utils.base64ToBitmap(face.getImgData()));
                                        if (template != null) {
                                            FaceData user = new FaceData();
                                            user.setName(face.getName());
                                            user.setFace(template);
                                            user.setIdno(face.getUserId());
                                            BlogDao.insertOrUpdateBlogItem(user);
                                            ZKLiveFaceManager.getInstance().dbAdd(face.getUserId(), template);
                                            template = null;
                                            Message msg = handler.obtainMessage(number);
                                            Bundle bundle = new Bundle();
                                            bundle.putString("size", rsData.size() + "");
                                            msg.setData(bundle);
                                            handler.sendMessage(msg);
                                        }
                                    }
                                    Message msg = handler.obtainMessage(end);
                                    handler.sendMessage(msg);
                                }
                            });
                        }
                    }
                });
    }

    private byte[] template;

    @Override
    protected boolean isRegisterEventBusHere() {
        return false;
    }

    @Override
    protected void eventBusResult(EventCenter eventCenter) {

    }

    private ExecutorService executorService;

    @Override
    protected void initDatas() {
        //??????????????????
        executorService = newSingleThreadExecutor();
    }


    public static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(5, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }
    private ProgressDialog progressDialog;
    private int number = 1;
    private int end = 2;

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                Bundle bundle = msg.getData();
                String size = bundle.getString("size");
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setTitle("?????????");
                    progressDialog.setMessage("????????????????????????");
                    progressDialog.setMax(Integer.parseInt(size));
                    progressDialog.setProgress(1);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.show();
                } else {
                    progressDialog.incrementProgressBy(1);
                }
                break;
            case 2:
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Toasty.success(mContext, "??????????????????", Toast.LENGTH_SHORT, false).show();
                startActivity(MainActivity.class);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void permisson() {
        List<PermissionItem> permissonItems = new ArrayList<PermissionItem>();
        permissonItems.add(new PermissionItem(Manifest.permission.READ_PHONE_STATE, "??????????????????", R.drawable.permission_ic_phone));
        permissonItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "????????????", R.drawable.permission_ic_storage));
        permissonItems.add(new PermissionItem(Manifest.permission.CAMERA, "??????", R.drawable.permission_ic_camera));
//        permissonItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "????????????", R.drawable.permission_ic_location));
        HiPermission.create(this)
                .permissions(permissonItems)
                .filterColor(ResourcesCompat.getColor(getResources(), R.color.app_theme, getTheme()))
                .style(R.style.PermissionBlueStyle)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        //showToast("????????????????????????");
                        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this, R.style.AlertDialogCustom);
                        builder.setTitle("?????????????????????");
                        builder.setMessage("??????????????????-??????-??????????????????");
                        builder.setCancelable(false);
                        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionPageUtils pageUtils = new PermissionPageUtils(FirstActivity.this);
                                pageUtils.jumpPermissionPage();
                                dialog.dismiss();
                                finish();
                            }
                        });
                        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        builder.create().show();
                    }

                    @Override
                    public void onFinish() {
                        B();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {

                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

}
