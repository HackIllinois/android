package org.hackillinois.android.view.custom.ticker

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import org.hackillinois.android.R

class HalfTicker(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var gapPercentage = 0F
    private var tickerBackgroundColor: Int = Color.WHITE
    private var tickerCornerRadius: Float = 0F
    private var tickerBackgroundRectF: RectF
    private var tickerText: String
    private var tickerTextSize: Float = 0F
    private var textColor: Int = Color.WHITE

    private var isTopHalf: Boolean = true

    private val textBounds = Rect()
    private var bitmapCanvas = Canvas()
    private var bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    init {
        context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.HalfTicker,
                0, 0).apply {

            try {
                tickerText = getString(R.styleable.HalfTicker_ticker_text) ?: ""
                isTopHalf = getBoolean(R.styleable.HalfTicker_is_top_half, true)
            } finally {
                recycle()
            }
        }

        val gapHeight = gapPercentage * height
        tickerBackgroundRectF = RectF(0F, 0F, width.toFloat(), height - gapHeight)
    }

    private val fillPaint = Paint().apply {
        color = tickerBackgroundColor
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint().apply {
        color = textColor
        textSize = tickerTextSize
        typeface = ResourcesCompat.getFont(context, R.font.montserrat_semi_bold)
        isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val gapHeight = gapPercentage * h
        bitmap = Bitmap.createBitmap(w, (h - gapHeight).toInt(), Bitmap.Config.ARGB_8888)
        bitmapCanvas.setBitmap(bitmap)
        tickerBackgroundRectF.set(0F, 0F, width.toFloat(), height - gapHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val gapHeight = gapPercentage * height
        textPaint.getTextBounds(tickerText, 0, tickerText.length, textBounds)
        val textWidth = textBounds.left + textBounds.width()
        val textHeight = textBounds.bottom + textBounds.height()

        if (isTopHalf) {
            bitmapCanvas.drawTopRoundRect(tickerBackgroundRectF, fillPaint, tickerCornerRadius)

            bitmapCanvas.drawText(
                tickerText,
                width.toFloat() / 2 - textWidth / 2,
                height.toFloat() + textHeight / 2,
                textPaint
            )

            canvas?.drawBitmap(bitmap, 0F, 0F, null)
        } else {
            bitmapCanvas.drawBottomRoundRect(tickerBackgroundRectF, fillPaint, tickerCornerRadius)

            bitmapCanvas.drawText(
                tickerText,
                width.toFloat() / 2 - textWidth / 2,
                textHeight / 2F - gapHeight,
                textPaint
            )

            canvas?.drawBitmap(bitmap, 0F, gapHeight, null)
        }
    }

    fun setProperties(
        gapPercentage: Float,
        tickerBackgroundColor: Int,
        tickerCornerRadius: Float,
        tickerTextSize: Float,
        textColor: Int) {
        this.gapPercentage = gapPercentage
        this.tickerCornerRadius = tickerCornerRadius

        this.tickerBackgroundColor = tickerBackgroundColor
        this.fillPaint.color = tickerBackgroundColor

        this.tickerTextSize = tickerTextSize
        this.textColor = textColor
        this.textPaint.apply {
            color = textColor
            textSize = tickerTextSize
        }
        invalidate()
    }

    fun setText(text: String?) {
        tickerText = text ?: "00"
        invalidate()
    }
}
