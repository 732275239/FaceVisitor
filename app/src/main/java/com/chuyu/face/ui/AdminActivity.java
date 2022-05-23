package com.chuyu.face.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.chuyu.face.R;
import com.chuyu.face.base.BaseActivity;
import com.chuyu.face.base.BaseAppManager;
import com.chuyu.face.base.URLs;
import com.chuyu.face.bean.BlogDao;
import com.chuyu.face.bean.FaceData;
import com.chuyu.face.bean.faces;
import com.chuyu.face.dialog.SetUrlDialogTwo;
import com.chuyu.face.http.OkHttp;
import com.chuyu.face.http.OkHttpCallBack;
import com.chuyu.face.tools.Base64_Utils;
import com.chuyu.face.tools.EventBus.EventCenter;
import com.chuyu.face.tools.ZKLiveFaceManager;
import com.chuyu.face.tools.share.SharedPreferencesUtils;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialog.interfaces.OnInputDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;


/**
 * @author Zoello
 * @description: 管理员界面
 * @date : 2021/4/29 15:36
 */
public class AdminActivity extends BaseActivity {

    private ImageView topBarLeftImg;
    private TextView topBarTitleTv;
    private LinearLayout modifyIP, outapp, filllight, update, changepassword;
    private Switch visitorIn, visitorOut;
    private BubbleSeekBar similarity;
    private ExecutorService executorService;

    @Override
    protected void initView() {
        boolean visIn = (boolean) SharedPreferencesUtils.getParam(this, "visitorIn", false);
        boolean visOut = (boolean) SharedPreferencesUtils.getParam(this, "visitorOut", false);
        int minScore = (int) SharedPreferencesUtils.getParam(this, "minScore", 76);
        topBarLeftImg = findViewById(R.id.top_bar_leftImg);
        topBarTitleTv = findViewById(R.id.top_bar_titleTv);
        topBarTitleTv.setText("管理员设置");
        topBarLeftImg.setOnClickListener(this);
        update = findViewById(R.id.update);
        modifyIP = findViewById(R.id.modifyIP);
        filllight = findViewById(R.id.filllight);
        changepassword = findViewById(R.id.changepassword);
        outapp = findViewById(R.id.outapp);
        visitorIn = findViewById(R.id.visitorIn);
        visitorOut = findViewById(R.id.visitorOut);
        similarity = findViewById(R.id.similarity);
        similarity.setProgress(minScore);
        visitorIn.setChecked(visIn);
        visitorOut.setChecked(visOut);
        changepassword.setOnClickListener(this);
        modifyIP.setOnClickListener(this);
        update.setOnClickListener(this);
        filllight.setOnClickListener(this);
        outapp.setOnClickListener(this);
        similarity.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                SharedPreferencesUtils.setParam(mContext, "minScore", progress);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
            }
        });
        Toasty.error(mContext, "请勿随意改动功能设置！！！！", Toast.LENGTH_LONG, false).show();
    }

    @Override
    protected void initDatas() {
        //创建单线程池
        executorService = newSingleThreadExecutor();
        visitorIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setParam(mContext, "visitorIn", isChecked);
            }
        });
        visitorOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferencesUtils.setParam(mContext, "visitorOut", isChecked);
            }
        });
    }

    public static ExecutorService newSingleThreadExecutor() {
        return new ThreadPoolExecutor(5, 10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_admin;
    }

    @Override
    protected View getLoadingTargetView() {
        return null;
    }

    @Override
    protected boolean isRegisterEventBusHere() {
        return true;
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
//                EventBus.getDefault().post(new EventCenter(EventCode.CODE1, ""));
                startActivity(MainActivity.class);
                finish();
                break;
            //修改IP
            case R.id.modifyIP:
                String url = (String) SharedPreferencesUtils.getParam(mContext, "url", "");
                // 1进 2出
                int inOrOut = (int) SharedPreferencesUtils.getParam(mContext, "inOrOut", 0);

                SetUrlDialogTwo urlDialog = new SetUrlDialogTwo(url, inOrOut);
                urlDialog.show(AdminActivity.this);
                urlDialog.setYesOnclickListener((s, inOrOut1) -> {
                    URLs.HOST_URL = s;
                    SharedPreferencesUtils.setParam(mContext, "url", s);
                    // 1进 2出
                    SharedPreferencesUtils.setParam(mContext, "inOrOut", inOrOut1);
                });
                break;
            //补光灯
            case R.id.filllight:
                startActivity(LightTimeActivity.class);
                break;
            case R.id.update:
                ZKLiveFaceManager.getInstance().del();
                getHumanFace();
                break;
            case R.id.changepassword:
                InputDialog.show(AdminActivity.this, "修改密码", "请输入6位数字密码", "确定", "取消")
                        .setInputInfo(new InputInfo()
                                .setMAX_LENGTH(6)
                                .setInputType(InputType.TYPE_CLASS_NUMBER)
                        )
                        .setOnOkButtonClickListener((baseDialog, v1, inputStr) -> {
                            SharedPreferencesUtils.setParam(mContext, "password", inputStr);
                            return false;
                        });
                break;
            case R.id.outapp:
                ZKLiveFaceManager.getInstance().del();
                sendBroadcast(new Intent("SHOW_NAVIGATION"));
                sendBroadcast(new Intent("SHOW_STATUS"));
                BaseAppManager.getInstance().clearAll();
                finish();
                break;
            default:
                break;
        }
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
                    progressDialog.setTitle("请稍后");
                    progressDialog.setMessage("正在解析人脸数据");
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
                Toasty.success(mContext, "人脸更新完成", Toast.LENGTH_SHORT, false).show();
                break;
            default:
                break;
        }
    }


    private void getHumanFace() {
        OkHttp.postArray(this, true, URLs.listUserFace, "",
                new OkHttpCallBack<ArrayList<faces>>(new TypeToken<ArrayList<faces>>() {
                }) {
                    @Override
                    public void onError() {
                        super.onError();
                        Toasty.error(mContext, "网络错误", Toast.LENGTH_SHORT, false).show();
                    }

                    @Override
                    public void onFailure(String eCode, String eMsg) {
                        super.onFailure(eCode, eMsg);
                        Toasty.error(mContext, "获取失败", Toast.LENGTH_SHORT, false).show();
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
                                        byte[] template = ZKLiveFaceManager.getInstance().getTemplateFromBitmap(Base64_Utils.base64ToBitmap(face.getImgData()));
                                        if (template != null) {
                                            FaceData user = new FaceData();
                                            user.setName(face.getName());
                                            user.setFace(template);
                                            user.setIdno(face.getUserId());
                                            BlogDao.insertOrUpdateBlogItem(user);
                                            ZKLiveFaceManager.getInstance().dbAdd(face.getUserId(), template);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
