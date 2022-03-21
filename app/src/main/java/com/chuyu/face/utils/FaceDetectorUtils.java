package com.chuyu.face.utils;

import android.graphics.Bitmap;
import android.media.FaceDetector;
import android.util.Log;


/**
 * @author Zoello
 */
public class FaceDetectorUtils {
    private static final String TAG = "abc";
    private static FaceDetectorUtils mFaceDetectorUtils;

    public static FaceDetectorUtils getInstance() {
        if (mFaceDetectorUtils == null) {
            synchronized (Nv21ToBitmapUtils.class) {
                if (mFaceDetectorUtils == null) {
                    mFaceDetectorUtils = new FaceDetectorUtils();
                }
            }
        }
        return mFaceDetectorUtils;
    }

    private FaceDetector.Face[] faces = new FaceDetector.Face[1];

    //接口不支持同时上传多张小脸,只取一张
    public Bitmap getCutBitmap(Bitmap bitmap) {
        Bitmap cutBitmap = null;
        //由于Android内存有限，图片太大的话，会出现无法加载图片的异常,图片的格式必须为Bitmap RGB565格式
        Bitmap bitmapDetect = bitmap.copy(Bitmap.Config.RGB_565, true);
        FaceDetector faceDetector = new FaceDetector(bitmapDetect.getWidth(), bitmapDetect.getHeight(), 1);
        faceDetector.findFaces(bitmapDetect, faces);
        //检测到人脸
        if (faces[0] != null) {
//            Log.i(TAG, "监测到人脸");
            cutBitmap = CircleBitmapUtils.circleBitmap(faces, bitmap);

        }
        if (!bitmap.isRecycled()){
            bitmap.recycle();
        }
        if (!bitmapDetect.isRecycled()){
            bitmapDetect.recycle();
        }
        return cutBitmap;
    }
}
