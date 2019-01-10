package org.hackillinois.android.view.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.dinuscxj.refresh.IRefreshStatus;

import org.hackillinois.android.R;

public class CustomRefreshView extends View implements IRefreshStatus {
    private static final int ANIMATION_DURATION = 888;

    private Drawable starfish;
    private RectF bounds;

    private ValueAnimator rotateAnimator;
    private float degrees;

    public CustomRefreshView(Context context) {
        this(context, null);
    }

    public CustomRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        starfish = getResources().getDrawable(R.drawable.starfish);
        bounds = new RectF();
        degrees = 0;
    }

    private void startAnimator() {
        rotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        rotateAnimator.setInterpolator(new LinearInterpolator());
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float rotations = (float) animation.getAnimatedValue();
                setDegrees(rotations * 360.0f);
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

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.rotate(degrees, bounds.centerX(), bounds.centerY());
        starfish.draw(canvas);
    }

    protected void onDetachedFromWindow() {
        resetAnimator();
        super.onDetachedFromWindow();
    }

    private void setDegrees(float degrees) {
        this.degrees = degrees;
        postInvalidate();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float centerX = w / 2.0f;
        float centerY = h / 2.0f;
        float radius = Math.min(w, h) / 3.0f;

        bounds.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        starfish.setBounds((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom);
    }

    public void reset() {
        resetAnimator();
        degrees = 0;
    }

    public void refreshing() {
        startAnimator();
    }

    public void refreshComplete() {}

    public void pullToRefresh() {}

    public void releaseToRefresh() {}

    public void pullProgress(float pullDistance, float pullProgress) {}
}
