package org.hackillinois.android.view.home

import android.os.CountDownTimer
import org.hackillinois.android.common.isBeforeNow
import org.hackillinois.android.common.timeUntilMs
import org.hackillinois.android.model.TimesWrapper
import java.util.*

class CountdownManager(val listener: CountDownListener) {

    // A2021-04-09 17:00:00
    private val eventStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618005600000
    }

    // 2021-04-09 18:00:00
    private val hackingStartTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618009200000
    }

    // 2021-04-11 18:00:00
    private val hackingEndTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1618182000000
    }

    private var times = listOf(eventStartTime, hackingStartTime, hackingEndTime)
    // placeholders in case design team decides to change this
    private val titles = listOf("HackIllinois Begins In", "Hacking Begins In", "Hacking Ends In", "What's Cookin?")

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
