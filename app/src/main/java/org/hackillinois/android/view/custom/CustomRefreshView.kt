package org.hackillinois.android.view.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.dinuscxj.refresh.IRefreshStatus
import org.hackillinois.android.R
import kotlin.math.min

class CustomRefreshView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), IRefreshStatus {
    private val arcBounds = RectF()
    private val paint = Paint()
    private var startDegrees = 0f
    private var swipeDegrees = 0f
    private var strokeWidth = 0f
    private var hasTriggeredRefresh = false
    private var rotateAnimator: ValueAnimator? = null

    init {
        initData()
        initPaint()
    }

    private fun initData() {
        val density = resources.displayMetrics.density
        strokeWidth = density * DEFAULT_STROKE_WIDTH
        startDegrees = DEFAULT_START_DEGREES.toFloat()
        swipeDegrees = 0.0f
    }

    private fun initPaint() {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = ContextCompat.getColor(context, R.color.colorPrimary)
    }

    private fun startAnimator() {
        rotateAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).apply {
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                val rotateProgress = animation.animatedValue as Float
                setStartDegrees(DEFAULT_START_DEGREES + rotateProgress * 360)
            }
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = ANIMATION_DURATION.toLong()
            start()
        }
    }

    private fun resetAnimator() {
        if (rotateAnimator != null) {
            rotateAnimator?.cancel()
            rotateAnimator?.removeAllUpdateListeners()
            rotateAnimator = null
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawArc(canvas)
    }

    override fun onDetachedFromWindow() {
        resetAnimator()
        super.onDetachedFromWindow()
    }

    private fun drawArc(canvas: Canvas) {
        canvas.drawArc(arcBounds, startDegrees, swipeDegrees, false, paint)
    }

    private fun setStartDegrees(startDegrees: Float) {
        this.startDegrees = startDegrees
        postInvalidate()
    }

    private fun setSwipeDegrees(swipeDegrees: Float) {
        this.swipeDegrees = swipeDegrees
        postInvalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val radius = min(w, h) / 4.0f
        val centerX = w / 2.0f
        val centerY = h / 2.0f
        arcBounds[centerX - radius, centerY - radius, centerX + radius] = centerY + radius
        arcBounds.inset(strokeWidth / 2.0f, strokeWidth / 2.0f)
    }

    override fun reset() {
        resetAnimator()
        hasTriggeredRefresh = false
        startDegrees = DEFAULT_START_DEGREES.toFloat()
        swipeDegrees = 0.0f
    }

    override fun refreshing() {
        hasTriggeredRefresh = true
        swipeDegrees = MAX_ARC_DEGREE.toFloat()
        startAnimator()
    }

    override fun refreshComplete() {}
    override fun pullToRefresh() {}
    override fun releaseToRefresh() {}
    override fun pullProgress(pullDistance: Float, pullProgress: Float) {
        if (!hasTriggeredRefresh) {
            val swipeProgress = min(1.0f, pullProgress)
            setSwipeDegrees(swipeProgress * MAX_ARC_DEGREE)
        }
    }

    companion object {
        private const val MAX_ARC_DEGREE = 330
        private const val ANIMATION_DURATION = 888
        private const val DEFAULT_START_DEGREES = 285
        private const val DEFAULT_STROKE_WIDTH = 2
    }
}
