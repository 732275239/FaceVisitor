package com.bifan.detectlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * created by ： 四个角
 */
public class FaceRectView extends View implements IFaceRectView {
    private final int BORDER_COLOR = Color.GREEN;
    private final int BORDER_WITH = 5;
    private String mShowInfo = "追踪对象";
    private float leftX;
    private float rightX;
    private float topY;
    private float bottomY;

    public FaceRectView(Context context) {
        super(context);
        init();
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private Paint mTextPaint;
    private Paint paint;

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(BORDER_WITH);
        paint.setColor(BORDER_COLOR);
//        paint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(5);
        mTextPaint.setTextSize(20);
        mTextPaint.setColor(BORDER_COLOR);
    }

    @Override
    public void drawFaceBorder(FaceDetector.Face[] mFace, float simple) {
        FaceDetector.Face face = mFace[0];
        //可信度大于0.5才进行绘制
        if (face.confidence() > 0.5) {
            mShowInfo = "追踪对象";
            float eyeDistance = (float) (face.eyesDistance() * 1.5) / simple;
            //float faceWidth = eyeDistance * 5;
            face.getMidPoint(eyeMidPoint);
            eyeMidPoint.x = eyeMidPoint.x / simple;
            eyeMidPoint.y = eyeMidPoint.y / simple;

            leftX = eyeMidPoint.x - eyeDistance;
            rightX = eyeMidPoint.x + eyeDistance;
            topY = eyeMidPoint.y - eyeDistance;
            bottomY = eyeMidPoint.y + eyeDistance * 1.2f;

            postInvalidate();
        }
    }

    private float rectLength = 30;
    private PointF eyeMidPoint = new PointF();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawPath(borderPath, paint);
        float widith = rightX - leftX;
        rectLength = (widith) / 7;
        drawRect(canvas);
    }

    private void drawRect(Canvas canvas) {
        if (mShowInfo != null) {

            /**
             * 左上竖线
             */
            canvas.drawLine(leftX, topY, leftX, topY + rectLength, paint);
            /**
             * 左上横线
             */
            canvas.drawLine(leftX, topY, leftX + rectLength, topY, paint);
            /**
             * 右上竖线
             */
            canvas.drawLine(rightX, topY, rightX - rectLength, topY, paint);
            /**
             * 右上横线
             */
            canvas.drawLine(rightX, topY, rightX, topY + rectLength, paint);
            /**
             * 左下竖线
             */
            canvas.drawLine(leftX, bottomY, leftX, bottomY - rectLength, paint);
            /**
             * 左下横线
             */
            canvas.drawLine(leftX, bottomY, leftX + rectLength, bottomY, paint);

            /**
             * 右下竖线
             */
            canvas.drawLine(rightX, bottomY, rightX, bottomY - rectLength, paint);
            /**
             * 右下横线
             */
            canvas.drawLine(rightX, bottomY, rightX - rectLength, bottomY, paint);

//            绘制文字
//            canvas.drawText(mShowInfo, rightX, topY - 10, mTextPaint);
        }

    }

    @Override
    public void clearBorder() {
        mShowInfo = null;
        postInvalidate();
    }
}
