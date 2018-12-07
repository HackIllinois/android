package org.hackillinois.android.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dinuscxj.refresh.IRefreshStatus;

import org.hackillinois.android.R;

/**
 * Copied from library: changed it to have right color scheme
 */
public class CustomRefreshView extends View implements IRefreshStatus {
    private static final int MAX_ARC_DEGREE = 330;
    private static final int ANIMATION_DURATION = 888;
    private static final int DEFAULT_START_DEGREES = 285;
    private static final int DEFAULT_STROKE_WIDTH = 2;

    private final RectF arcBounds = new RectF();
    private final Paint paint = new Paint();

    private float startDegrees;
    private float swipeDegrees;

    private float strokeWidth;

    private boolean hasTriggeredRefresh;

    private ValueAnimator rotateAnimator;

    public CustomRefreshView(Context context) {
        this(context, null);
    }

    public CustomRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initData();
        initPaint();
    }

    private void initData() {
        float density = getResources().getDisplayMetrics().density;
        strokeWidth = density * DEFAULT_STROKE_WIDTH;

        startDegrees = DEFAULT_START_DEGREES;
        swipeDegrees = 0.0f;
    }

    private void initPaint() {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    private void startAnimator() {
        rotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rotateProgress = (float) animation.getAnimatedValue();
                setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360);
            }
        });
        rotateAnimator.setRepeatMode(ValueAnimator.RESTART);
        rotateAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotateAnimator.setDuration(ANIMATION_DURATION);

        rotateAnimator.start();
    }

    private void resetAnimator() {
        if (rotateAnimator != null) {
            rotateAnimator.cancel();
            rotateAnimator.removeAllUpdateListeners();

            rotateAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        resetAnimator();
        super.onDetachedFromWindow();
    }

    private void drawArc(Canvas canvas) {
        canvas.drawArc(arcBounds, startDegrees, swipeDegrees, false, paint);
    }

    private void setStartDegrees(float startDegrees) {
        this.startDegrees = startDegrees;
        postInvalidate();
    }

    public void setSwipeDegrees(float swipeDegrees) {
        this.swipeDegrees = swipeDegrees;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float radius = Math.min(w, h) / 2.0f;
        float centerX = w / 2.0f;
        float centerY = h / 2.0f;

        arcBounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        arcBounds.inset(strokeWidth / 2.0f, strokeWidth / 2.0f);
    }

    @Override
    public void reset() {
        resetAnimator();

        hasTriggeredRefresh = false;
        startDegrees = DEFAULT_START_DEGREES;
        swipeDegrees = 0.0f;
    }

    @Override
    public void refreshing() {
        hasTriggeredRefresh = true;
        swipeDegrees = MAX_ARC_DEGREE;

        startAnimator();
    }

    @Override
    public void refreshComplete() { }

    @Override
    public void pullToRefresh() { }

    @Override
    public void releaseToRefresh() { }

    @Override
    public void pullProgress(float pullDistance, float pullProgress) {
        if (!hasTriggeredRefresh) {
            float swipeProgress = Math.min(1.0f, pullProgress);
            setSwipeDegrees(swipeProgress * MAX_ARC_DEGREE);
        }
    }
}
