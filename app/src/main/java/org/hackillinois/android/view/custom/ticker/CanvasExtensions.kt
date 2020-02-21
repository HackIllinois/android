package org.hackillinois.android.view.custom.ticker

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

fun Canvas.drawTopRoundRect(rect: RectF, paint: Paint, radius: Float) {
    drawRoundRect(rect, radius, radius, paint)
    drawRect(rect.left, rect.top + radius, rect.right, rect.bottom, paint)
}

fun Canvas.drawBottomRoundRect(rect: RectF, paint: Paint, radius: Float) {
    drawRoundRect(rect, radius, radius, paint)
    drawRect(rect.left, rect.top, rect.right, rect.bottom - radius, paint)
}
