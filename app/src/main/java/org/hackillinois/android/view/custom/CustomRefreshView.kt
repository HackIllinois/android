package org.hackillinois.android.view.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

import com.dinuscxj.refresh.IRefreshStatus

import org.hackillinois.android.R

class CustomRefreshView constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), IRefreshStatus {

    private val ANIMATION_DURATION = 888
    private val starfish: Drawable = resources.getDrawable(R.drawable.starfish)
    private val bounds: RectF = RectF()

    private var rotateAnimator: ValueAnimator? = null
    private var degrees: Float = 0F

    private fun startAnimator() {
        rotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val rotations = animation.animatedValue as Float
                setDegrees(rotations * 360.0f)
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = ANIMATION_DURATION.toLong()
            start()
        }

    }

    private fun resetAnimator() {
        rotateAnimator?.let {
            it.cancel()
            it.removeAllUpdateListeners()
            rotateAnimator = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.rotate(degrees, bounds.centerX(), bounds.centerY())
        starfish.draw(canvas)
    }

    override fun onDetachedFromWindow() {
        resetAnimator()
        super.onDetachedFromWindow()
    }

    private fun setDegrees(degrees: Float) {
        this.degrees = degrees
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = Math.min(w, h) / 3.0f
        bounds.set(0f, 0f, 2 * radius, 2 * radius)
        starfish.setBounds(
            bounds.left.toInt(),
            bounds.top.toInt(),
            bounds.right.toInt(),
            bounds.bottom.toInt()
        )
    }

    override fun reset() {
        resetAnimator()
        degrees = 0f
    }

    override fun refreshing() {
        startAnimator()
    }

    override fun refreshComplete() {}
    override fun pullToRefresh() {}
    override fun releaseToRefresh() {}
    override fun pullProgress(pullDistance: Float, pullProgress: Float) {}
}
