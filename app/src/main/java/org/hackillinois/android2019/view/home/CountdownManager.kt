package org.hackillinois.android2019.view.home

import android.os.CountDownTimer
import org.hackillinois.android2019.common.isBeforeNow
import org.hackillinois.android2019.common.timeUntilMs
import java.util.*

class CountdownManager(val listener: CountDownListener) {

    private val eventStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 22, 17, 0, 0)
    }

    private val hackingStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 22, 23, 0, 0)
    }

    private val hackingEndTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 24, 11, 0, 0)
    }

    private val times = listOf(eventStartTime, hackingStartTime, hackingEndTime)
    private val titles = listOf("EVENT STARTS IN", "HACKING STARTS IN", "HACKING ENDS IN", "THANKS FOR COMING!")

    private var timer: CountDownTimer? = null
    private var state = 0

    private val refreshRateMs = 500L

    fun start() {
        while (state < times.size && times[state].isBeforeNow()) {
            state++
        }
        startTimer()
    }

    private fun startTimer() {
        if (state >= times.size) {
            listener.updateTitle(titles[state])
            return
        }
        listener.updateTitle(titles[state])

        val millisTillTimerFinishes = times[state].timeUntilMs()

        timer = object : CountDownTimer(millisTillTimerFinishes, refreshRateMs) {
            override fun onTick(millisUntilFinished: Long) {
                val timeUntil = times[state].timeUntilMs()
                listener.updateTime(timeUntil)
            }

            override fun onFinish() {
                state++
                startTimer()
            }
        }.start()
    }

    fun onPause() {
        timer?.cancel()
        timer = null
    }

    fun onResume() {
        if (timer == null) {
            start()
        }
    }

    interface CountDownListener {
        fun updateTime(timeUntil: Long)
        fun updateTitle(newTitle: String)
    }
}
