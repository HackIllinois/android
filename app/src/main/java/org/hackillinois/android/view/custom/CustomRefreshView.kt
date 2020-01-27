package org.hackillinois.android.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import com.dinuscxj.refresh.IRefreshStatus
import org.hackillinois.android.R

class CustomRefreshView constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), IRefreshStatus {

    private var degrees: Float = 0F

    private var carLottieView: LottieAnimationView = LayoutInflater.from(context).inflate(R.layout.car_lottie_view, this, false) as LottieAnimationView

    init {
        addView(carLottieView)
    }

    override fun onDetachedFromWindow() {
        carLottieView.playAnimation()
        super.onDetachedFromWindow()
    }

    override fun reset() {
        carLottieView.cancelAnimation()
        degrees = 0f
    }

    override fun refreshing() {
        carLottieView.playAnimation()
    }

    override fun refreshComplete() {}
    override fun pullToRefresh() {}
    override fun releaseToRefresh() {}
    override fun pullProgress(pullDistance: Float, pullProgress: Float) {}
}
