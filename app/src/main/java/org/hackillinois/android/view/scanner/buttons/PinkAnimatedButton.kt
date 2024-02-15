package org.hackillinois.android.view.scanner.buttons

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import org.hackillinois.android.R

class PinkAnimatedButton : androidx.appcompat.widget.AppCompatButton {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val scale = resources.displayMetrics.density
            val dpAsPixels20 = (20 * scale + 0.5f).toInt()
            val dpAsPixels30 = (30 * scale + 0.5f).toInt()
            if (event.action == MotionEvent.ACTION_DOWN) {
                setPadding(0, dpAsPixels30, 0, dpAsPixels20)
                setBackgroundResource(R.drawable.pink_scanner_button_pressed)
            } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                setPadding(0, dpAsPixels20, 0, dpAsPixels30)
                setBackgroundResource(R.drawable.pink_scanner_button)
            }
        }
        return super.onTouchEvent(event)
    }
}
