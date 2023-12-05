package org.hackillinois.android.view.home

import android.os.CountDownTimer
import org.hackillinois.android.common.isBeforeNow
import org.hackillinois.android.common.timeUntilMs
import java.util.*

class CountdownManager(val listener: CountDownListener) {

    //  2024-02-23 15:00:00
    private val eventStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708722000000
    }

    // 2023-02-24 19:00:00
    private val hackingStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1677286800000
    }

    // 2023-02-26 9:00:00
    private val hackingEndTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1677423600000
    }

    private var times = listOf(eventStartTime, hackingStartTime, hackingEndTime)
    private val titles = listOf("HACKILLINOIS BEGINS IN", "HACKING BEGINS IN", "HACKING ENDS IN", "MEMORIES MADE")

    private var timer: CountDownTimer? = null
    private var state = 0

    private val refreshRateMs = 500L

    fun start() {
        // find current state of the countdown in terms of timestamps
        while (state < times.size && times[state].isBeforeNow()) {
            state++
        }
        startTimer()
    }

    private fun startTimer() {
        // if past the last timestamp, don't start another timer
        if (state >= times.size) {
            listener.updateTitle(titles[titles.size - 1]) // set to be last title
            return
        }

        // else set the current title and start timer until next timestamp
        listener.updateTitle(titles[state])
        val millisTillTimerFinishes = times[state].timeUntilMs()

        timer = object : CountDownTimer(millisTillTimerFinishes, refreshRateMs) {
            // update the time on each tick
            override fun onTick(millisUntilFinished: Long) {
                val timeUntil = times[state].timeUntilMs()
                listener.updateTime(timeUntil)
            }

            // increment the state when timer is finished
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
