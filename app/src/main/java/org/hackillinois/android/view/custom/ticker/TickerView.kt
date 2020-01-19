package org.hackillinois.android.view.custom.ticker

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import org.hackillinois.android.R

class TickerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private var topUnderTicker: HalfTicker
    private var topOverTicker: HalfTicker

    private var bottomUnderTicker: HalfTicker
    private var bottomOverTicker: HalfTicker

    private var allTickers: List<HalfTicker>

    init {
        val inflatedView = View.inflate(context, R.layout.ticker_layout, null)
        topUnderTicker = inflatedView.findViewById(R.id.top_lower_ticker)
        topOverTicker = inflatedView.findViewById(R.id.top_upper_ticker)

        bottomUnderTicker = inflatedView.findViewById(R.id.bottom_lower_ticker)
        bottomOverTicker = inflatedView.findViewById(R.id.bottom_upper_ticker)

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
        allTickers.map { it.setText(text) }
    }
}
