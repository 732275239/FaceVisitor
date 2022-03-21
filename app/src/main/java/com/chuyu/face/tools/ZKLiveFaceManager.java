package com.chuyu.face.tools;

import android.graphics.Bitmap;
import android.util.Log;

import com.zkteco.android.biometric.liveface56.ZKLiveFaceService;


public class ZKLiveFaceManager {
    private final String TAG = ZKLiveFaceManager.class.getSimpleName();
    private static ZKLiveFaceManager zkLiveFaceManager = null;
    private long context;
    private boolean isInit = false;
    public final int DEFAULT_VERIFY_SCORE = 76;

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public static ZKLiveFaceManager getInstance() {
        if (zkLiveFaceManager == null) {
            zkLiveFaceManager = new ZKLiveFaceManager();
        }
        return zkLiveFaceManager;
    }

    public boolean isAuthorized() {
        return ZKLiveFaceService.isAuthorized();
    }

    public String getHardwareId() {
        byte[] hwid = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        if (0 == ZKLiveFaceService.getHardwareId(hwid, size)) {
            String hwidStr = new String(hwid, 0, size[0]);
            Log.d(TAG, "machinecode:" + hwidStr);
            return hwidStr;
        } else {
            return null;
        }
    }

    public String getDeviceFingerprint() {
        byte[] hwid = new byte[32 * 1024];
        int[] size = new int[1];
        size[0] = 32 * 1024;
        if (0 == ZKLiveFaceService.getDeviceFingerprint(hwid, size)) {
            String hwidStr = new String(hwid, 0, size[0]);
            return hwidStr;
        } else {
            return null;
        }
    }

    public boolean setParameterAndInit(String path) {
        if (!isAuthorized()) {
            ZKLiveFaceService.setParameter(0, 1011, path.getBytes(), path.length());
        }
        long[] retContext = new long[1];
        int ret = ZKLiveFaceService.init(retContext);
        Log.i(TAG, "init ret = " + ret);
        if (ret == 0) {
            context = retContext[0];
            ZKLiveFaceService.dbClear(context);
            setInit(true);
            return true;
        } else {
            setInit(false);
            return false;
        }
    }

    public byte[] getTemplateFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int[] detectedFaces = new int[1];
        int ret = ZKLiveFaceService.detectFacesFromBitmap(context, bitmap, detectedFaces);
        if (ret != 0 || detectedFaces[0] <= 0) {
            return null;
        }
        long[] faceContext = new long[1];
        ret = ZKLiveFaceService.getFaceContext(context, 0, faceContext);
        if (ret != 0) {
            return null;
        }
        byte[] template = new byte[8 * 1024];
        int[] size = new int[1];
        int[] resverd = new int[1];
        size[0] = 8 * 1024;
        ret = ZKLiveFaceService.extractTemplate(faceContext[0], template, size, resverd);
        if (ret == 0) {
            return template;
        }
        return null;
    }

    public byte[] getTemplateFromNV21(byte[] nv21, int width, int heigh) {
        int[] detectedFaces = new int[1];
        int ret = ZKLiveFaceService.detectFacesFromNV21(context, nv21, width, heigh, detectedFaces);
        if (ret != 0 || detectedFaces[0] <= 0) {
            return null;
        }
        long[] faceContext = new long[1];
        ret = ZKLiveFaceService.getFaceContext(context, 0, faceContext);
        if (ret != 0) {
            return null;
        }
        //        人脸角度
        float[] yaw = new float[1];
        float[] pitch = new float[1];
        float[] roll = new float[1];
        ret = ZKLiveFaceService.getFacePose(faceContext[0], yaw, pitch, roll);
        if (ret != 0 || yaw[0] > 20 || pitch[0] > 20 || roll[0] > 20) {
            return null;
        }
//        活体检测
        int[] score = new int[1];
        ret = ZKLiveFaceService.getLiveness(faceContext[0], score);
        if (ret != 0 || score[0] < 75) {
            return null;
        }
//        矩形人脸监测
        int[] points = new int[8];
        //屏幕宽度500 高度700
        ret = ZKLiveFaceService.getFaceRect(faceContext[0], points, 8);
        if (ret == 0) {
            int p0x = points[0];
            int p0y = points[1];
            int p2x = points[4];
            int p2y = points[5];
//            //只允许人脸在屏幕100范围内
//            if (p0x < 50 || p0y < 100 || p2x > 450 || p2y > 600) {
//                return null;
//            }
            //人脸大小只允许  100 < x < 400
            int i = points[2] - p0x;
            if (i < 100 || i > 400) {
                return null;
            }
        } else {
            return null;
        }
        byte[] template = new byte[8 * 1024];
        int[] size = new int[1];
        int[] resverd = new int[1];
        size[0] = 8 * 1024;
        ret = ZKLiveFaceService.extractTemplate(faceContext[0], template, size, resverd);
        if (ret == 0) {
            return template;
        }
        return null;
    }

    public int verify(byte[] template1, byte[] template2) {
        int[] score = new int[1];
        int ret = ZKLiveFaceService.verify(context, template1, template2, score);
        Log.i(TAG, "verify ret = " + ret);
        if (ret == 0) {
            return score[0];
        }
        return 0;
    }

    //1：N 对比数据，缓冲区里面对比  minScore 最低识别分数
    public String identify(byte[] template,int minScore) {
        int[] score = new int[1];

        byte[] faceIDS = new byte[256];
        int[] maxRetCount = new int[1];
        maxRetCount[0] = 1;
        int ret = ZKLiveFaceService.dbIdentify(context, template, faceIDS, score, maxRetCount, minScore, 100);
        if (ret != 0) {
            return null;
        }
        return new String(faceIDS).trim().toString();
    }

    //添加到高速缓冲区，
    public boolean dbAdd(String id, byte[] template) {
        int ret = ZKLiveFaceService.dbAdd(context, id, template);
        if (ret == 0) {
            return true;
        }
        return false;
    }

    //清除高速缓冲区，
    public void del() {
        ZKLiveFaceService.dbClear(context);
    }
}
