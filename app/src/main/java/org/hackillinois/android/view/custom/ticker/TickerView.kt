package org.hackillinois.android.view.custom.ticker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import org.hackillinois.android.R

class TickerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var topUnderTicker: HalfTicker
    private var topOverTicker: HalfTicker

    private var bottomUnderTicker: HalfTicker
    private var bottomOverTicker: HalfTicker

    private var allTickers: List<HalfTicker>

    private var firstAnimation: Animation
    private var secondAnimation: Animation

    private var currentText: String? = null

    init {
        val inflatedView = View.inflate(context, R.layout.ticker_layout, null)
        topUnderTicker = inflatedView.findViewById(R.id.top_lower_ticker)
        topOverTicker = inflatedView.findViewById(R.id.top_upper_ticker)

        bottomUnderTicker = inflatedView.findViewById(R.id.bottom_lower_ticker)
        bottomOverTicker = inflatedView.findViewById(R.id.bottom_upper_ticker)

        firstAnimation = AnimationUtils.loadAnimation(context, R.anim.ticker_bottom_animation)
        secondAnimation = AnimationUtils.loadAnimation(context, R.anim.ticker_top_animation)

        firstAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                bottomOverTicker.visibility = View.INVISIBLE
                topOverTicker.visibility = View.VISIBLE
                bottomOverTicker.setText(currentText)
                topUnderTicker.setText(currentText)
            }

            override fun onAnimationRepeat(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                topOverTicker.visibility = View.INVISIBLE
                startSecondAnimation()
            }
        })

        secondAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                bottomOverTicker.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) { }

            override fun onAnimationEnd(animation: Animation) {
                topOverTicker.visibility = View.VISIBLE
                topOverTicker.setText(currentText)
                bottomUnderTicker.setText(currentText)
            }
        })

        allTickers = listOf(topUnderTicker, topOverTicker, bottomUnderTicker, bottomOverTicker)

        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TickerView,
                0, 0).apply {
            try {
                val textColor = getColor(R.styleable.TickerView_text_color, Color.WHITE)
                allTickers.map { it.textColor = textColor }
            } finally {
                recycle()
            }
        }

        addView(inflatedView)
    }

    fun setText(text: String) {
        if (text != currentText) {
            currentText = text
            if (currentText != null) {
                startFirstAnimation()
            }
        }
    }

    private fun startFirstAnimation() {
        topOverTicker.apply {
            clearAnimation()
            animation = firstAnimation
            startAnimation(firstAnimation)
        }
    }

    private fun startSecondAnimation() {
        bottomOverTicker.apply {
            clearAnimation()
            animation = secondAnimation
            startAnimation(secondAnimation)
        }
    }
}
