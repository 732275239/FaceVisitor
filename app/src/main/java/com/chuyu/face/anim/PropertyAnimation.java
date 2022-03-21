package com.chuyu.face.anim;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;


/**
 *名称：定义PropertyAnimation的动画类
 *创建人：xw
 *创建时间：2017/3/17 0017 下午 6:11
 *详细说明：
 */
public class PropertyAnimation {
    private Context context;
    private ObjectAnimator oA;

    public static PropertyAnimation instanceAnimation(Context context) {
        PropertyAnimation fragment = new PropertyAnimation(context);

        return fragment;
    }

    public PropertyAnimation(Context context) {
        this.context = context;
    }

    /**
     * @param needAnimationView
     * @param delay
     * @param dura
     * @param form
     * @param to
     * @return
     */
    public ObjectAnimator xMoveAnimate(Object needAnimationView,
                                       long delay, long dura, float form, float to) {
        oA = new ObjectAnimator();
        oA.setFloatValues(form, to);
        oA.setDuration(dura);
        oA.setPropertyName("x");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        return oA;
    }

    public ObjectAnimator rotationAnimate(Object needAnimationView,
                                          long delay, long dura, float formDegree, float toDegree) {
        oA = new ObjectAnimator();
        oA.setFloatValues(formDegree, toDegree);
        oA.setDuration(dura);
        oA.setPropertyName("rotation");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        return oA;
    }

    public ObjectAnimator yMoveAnimate(Object needAnimationView,
                                       long delay, long dura, float form, float to) {
        oA = new ObjectAnimator();
        oA.setFloatValues(form, to);
        oA.setDuration(dura);
        oA.setPropertyName("y");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        return oA;
    }

    public ObjectAnimator yMoveAnimateQuicken(Object needAnimationView,
                                              long delay, long dura, float form, float to) {
        oA = new ObjectAnimator();
        oA.setFloatValues(form, to);
        oA.setDuration(dura);
        oA.setInterpolator(new AccelerateInterpolator());
        oA.setPropertyName("y");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        return oA;
    }

    /**
     * 缩放动画
     *
     * @param needAnimationView
     * @param delay
     * @param dura
     * @param form
     * @param to
     * @param keyFrameCount
     * @return
     */
    public ObjectAnimator scaleAnimate(Object needAnimationView,
                                       long delay, long dura, float form, float to, int keyFrameCount) {
        Keyframe[] keyframesX = new Keyframe[keyFrameCount + 1];
        Keyframe[] keyframesY = new Keyframe[keyFrameCount + 1];
        float degree = (to - form) / (float) keyFrameCount;
        float timer = 1 / (float) keyFrameCount;
        for (int i = 0; i < keyFrameCount + 1; i++) {
            keyframesX[i] = Keyframe.ofFloat(timer * i, form + degree * i);
            keyframesY[i] = Keyframe.ofFloat(timer * i, form + degree * i);
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("scaleX", keyframesX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("scaleY", keyframesY);
        oA = ObjectAnimator.ofPropertyValuesHolder(needAnimationView, pvhX, pvhY);
        oA.setDuration(dura);
        oA.setStartDelay(delay);
        oA.setInterpolator(new OvershootInterpolator());
        oA.setTarget(needAnimationView);
        return oA;
    }

    /**
     * 按圆旋转动画
     *
     * @param needAnimationView 要旋转的控件
     * @param delay             延时
     * @param dura              动画时间
     * @param xAnchor           旋转轴X坐标
     * @param yAnchor           旋转轴Y坐标
     * @param radius            旋转半径
     * @param formDegree        起始角度
     * @param toDegree          结束角度
     * @param keyFrameCount     关键帧数量
     * @return
     */
    public ObjectAnimator parkingButtonAnimate(Object needAnimationView,
                                               long delay, long dura, float xAnchor, float yAnchor, float radius, float formDegree, float toDegree, int keyFrameCount) {
        Keyframe[] keyframesX = new Keyframe[keyFrameCount + 1];
        Keyframe[] keyframesY = new Keyframe[keyFrameCount + 1];
        float degree = (toDegree - formDegree) / (float) keyFrameCount;
        float timer = 1 / (float) keyFrameCount;
        for (int i = 0; i < keyFrameCount + 1; i++) {
            keyframesX[i] = Keyframe.ofFloat(timer * i, (float) (xAnchor - radius * Math.sin(degree * i + formDegree)));
            keyframesY[i] = Keyframe.ofFloat(timer * i, (float) (yAnchor - radius * Math.cos(degree * i + formDegree)));
        }
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofKeyframe("x", keyframesX);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("y", keyframesY);
        oA = ObjectAnimator.ofPropertyValuesHolder(needAnimationView, pvhX, pvhY);
        oA.setDuration(dura);
        oA.setStartDelay(delay);
        return oA;
    }

    /**
     * 渐变淡入动画
     *
     * @param needAnimationView
     * @param delay
     * @param dura
     * @return
     */
    public ObjectAnimator alphaAnimate(Object needAnimationView,
                                       long delay, long dura, float form, float to) {
        oA = new ObjectAnimator();
        oA.setFloatValues(form, to);
        oA.setDuration(dura);
        oA.setPropertyName("alpha");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        return oA;
    }

    /**
     * 渐变淡入动画
     *
     * @param needAnimationView
     * @param dura
     * @return
     */
    public ObjectAnimator alphaToHidAnimate(final Object needAnimationView, long dura) {
        oA = new ObjectAnimator();
        oA.setFloatValues(1.0f, 0.0f);
        oA.setDuration(dura);
        oA.setPropertyName("alpha");
        oA.setTarget(needAnimationView);
        oA.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ((View) needAnimationView).setVisibility(View.GONE);
                ((View) needAnimationView).setAlpha(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return oA;
    }
    /**
     * 淡入淡出动画，附带结束动作
     *
     * @param needAnimationView
     * @param delay
     * @param dura
     * @param action            needAnimationView消失还是显示 true 显示
     * @return
     */
    public ObjectAnimator alphaHidOrShow(final Object needAnimationView,
                                         long delay, long dura, final boolean action) {
        oA = new ObjectAnimator();
        if (action)
            oA.setFloatValues(0f, 1f);
        else
            oA.setFloatValues(1f, 0f);
        oA.setDuration(dura);
        oA.setPropertyName("alpha");
        oA.setStartDelay(delay);
        oA.setTarget(needAnimationView);
        if (needAnimationView instanceof View)
            oA.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (action)
                        ((View) needAnimationView).setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!action)
                        ((View) needAnimationView).setVisibility(View.INVISIBLE);

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        return oA;
    }

    /**
     * x轴抖动动画
     *
     * @param needAnimationView
     * @param delay
     * @param dura
     * @param range             抖动幅度
     * @return
     */
    public ObjectAnimator xShake(Object needAnimationView, long delay, long dura, float range) {
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -range),
                Keyframe.ofFloat(.26f, range),
                Keyframe.ofFloat(.42f, -range),
                Keyframe.ofFloat(.58f, range),
                Keyframe.ofFloat(.74f, -range),
                Keyframe.ofFloat(.90f, range),
                Keyframe.ofFloat(1f, 0f)
        );
        oA = ObjectAnimator.ofPropertyValuesHolder(needAnimationView, pvhTranslateX);
        oA.setDuration(dura);
        oA.setStartDelay(delay);
        return oA;
    }
}
