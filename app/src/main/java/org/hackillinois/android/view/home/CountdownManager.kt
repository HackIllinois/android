package org.hackillinois.android.view.home

import android.os.CountDownTimer
import org.hackillinois.android.common.isBeforeNow
import org.hackillinois.android.common.timeUntilMs
import org.hackillinois.android.model.TimesWrapper
import java.util.*

class CountdownManager(val listener: CountDownListener) {

    // placeholder time: April 9th 2021, 9pm
    private val eventStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618005600000
    }

    // placeholder time: April 10th 2021, 12am
    private val hackingStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618009200000
    }

    // placeholder time: April 11th 2021 12pm
    private val hackingEndTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618182000000
    }

    private var times = listOf(eventStartTime, hackingStartTime, hackingEndTime)
    // placeholders in case design team decides to change this
    private val titles = listOf("Countdown", "Countdown", "Countdown", "Countdown")

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

    fun setAPITimes(timesWrapper: TimesWrapper) {
        onPause()
        val apiEventStartTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = timesWrapper.data.eventStart * 1000L
        }

        val apiHackingStartTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = timesWrapper.data.hackStart * 1000L
        }

        val apiHackingEndTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = timesWrapper.data.hackEnd * 1000L
        }

        times = listOf(apiEventStartTime, apiHackingStartTime, apiHackingEndTime)
        state = 0
        startTimer()
    }

    interface CountDownListener {
        fun updateTime(timeUntil: Long)
        fun updateTitle(newTitle: String)
    }
}
