package com.chuyu.face.cusview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

import com.chuyu.face.tools.share.SharedPreferencesUtils;

import java.util.ArrayList;

public class FaceRectView extends View {

    private Paint mPaint;
    private Paint mTextPaint;
    private Rect rect;
    private String mCorlor = "#00FF00";
    private String mShowInfo = "追踪对象";
    private int rectLength = 30;
    private ArrayList<Rect> faceRects;

    public FaceRectView(Context context) {
        super(context);
        initPaint(context);
    }

    public FaceRectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initPaint(context);
    }

    public FaceRectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint(context);
    }

    private void initPaint(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4);
        mPaint.setColor(Color.parseColor(mCorlor));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(Color.parseColor(mCorlor));
    }

    public void drawFaceRect(Rect facerect) {
        this.rect = facerect;
        postInvalidate();
    }

    public void drawFaceRects(Camera.Face[] faces, View mView, int cameraPosition) {
        faceRects = new ArrayList();
        for (int i = 0; i < faces.length; i++) {
            Rect faceRect = new Rect(faces[i].rect.left, faces[i].rect.top, faces[i].rect.right, faces[i].rect.bottom);
            Rect dstRect = transForm(faceRect, mView.getWidth(), mView.getHeight(), (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT));
            faceRects.add(dstRect);
        }
        postInvalidate();
    }

    public void drawFaceRects(Camera.Face faces, View mView, int cameraPosition) {
        faceRects = new ArrayList();
        Rect faceRect = new Rect(faces.rect.left, faces.rect.top, faces.rect.right, faces.rect.bottom);
        Rect dstRect = transForm(faceRect, mView.getWidth(), mView.getHeight(), (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT));
        faceRects.add(dstRect);
        postInvalidate();
    }

    public void drawFaceInfo(String faceInfo) {
        this.mShowInfo = faceInfo;
        postInvalidate();
    }

    public void clearRect() {
        rect = null;
        mShowInfo = null;
        if (faceRects != null) {
            faceRects.clear();
            faceRects = null;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (faceRects != null && faceRects.size() > 0) {
            for (int i = 0; i < faceRects.size(); i++) {
                Rect rect = faceRects.get(i);
                int widith = rect.right - rect.left;
                rectLength = (widith) / 7;
                drawRect(rect, canvas);
            }
        }

    }

    private void drawRect(Rect rect, Canvas canvas) {
        if (rect != null) {
            /**
             * 左上竖线
             */
            canvas.drawLine(rect.left, rect.top, rect.left, rect.top + rectLength, mPaint);
            /**
             * 左上横线
             */
            canvas.drawLine(rect.left, rect.top, rect.left + rectLength, rect.top, mPaint);
            /**
             * 右上竖线
             */
            canvas.drawLine(rect.right, rect.top, rect.right - rectLength, rect.top, mPaint);
            /**
             * 右上横线
             */
            canvas.drawLine(rect.right, rect.top, rect.right, rect.top + rectLength, mPaint);
            /**
             * 左下竖线
             */
            canvas.drawLine(rect.left, rect.bottom, rect.left, rect.bottom - rectLength, mPaint);
            /**
             * 左下横线
             */
            canvas.drawLine(rect.left, rect.bottom, rect.left + rectLength, rect.bottom, mPaint);

            /**
             * 右下竖线
             */
            canvas.drawLine(rect.right, rect.bottom, rect.right, rect.bottom - rectLength, mPaint);
            /**
             * 右下横线
             */
            canvas.drawLine(rect.right, rect.bottom, rect.right - rectLength, rect.bottom, mPaint);


            canvas.drawText(mShowInfo, rect.left, rect.top - 10, mTextPaint);

        }
    }

    public Rect transForm(Rect faceRect, int sfW, int sfH, boolean mirror) {
        Matrix matrix = new Matrix();

        matrix.setScale(1f, mirror ? -1f : 1f);
        matrix.postRotate(90f);
        matrix.postScale(sfW / 2000f, sfH / 2000f);
        if (mirror) {
            //镜像垂直翻转
            matrix.postScale(1, -1);
            //镜像水平翻转
            matrix.postScale(-1, 1);
        }
        matrix.postTranslate(sfW / 2f, sfH / 2f);
        RectF srcRect = new RectF(faceRect);
        RectF dstRect = new RectF(0f, 0f, 0f, 0f);
        matrix.mapRect(dstRect, srcRect);

        return new Rect((int) dstRect.left, (int) dstRect.top, (int) dstRect.right, (int) dstRect.bottom);
    }

}
