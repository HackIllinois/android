package org.hackillinois.android.view.home

import android.os.CountDownTimer
import org.hackillinois.android.R
import org.hackillinois.android.common.isBeforeNow
import org.hackillinois.android.common.timeUntilMs
import java.util.*

class EventProgressManager(val listener: CountDownListener) {

    // CORRECT TIMES ARE NOT SET YET
    // 02-28-2025 : 14:30
    private val checkInTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740774600000
    }

    // 02-28-2025 : 15:00
    private val scavengerHuntTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740776400000
    }

    // 02-28-2025 : 17:00
    private val openingCeremonyTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740783600000
    }

    // 02-28-2025 : 18:00
    private val hackingTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740787200000
    }

    // 03-02-2025 11:30
    private val projectShowcaseTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740936600000
    }

    // 03-02-2025 15:00
    private val closingCeremonyTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740949200000
    }

    // 03-02-2025 16:00
    private val afterHackathonTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1740952800000
    }

    private var times = listOf(checkInTime, scavengerHuntTime, openingCeremonyTime, hackingTime, projectShowcaseTime, closingCeremonyTime, afterHackathonTime)
    private var backgrounds = listOf(R.drawable.home_stage1, R.drawable.home_stage2, R.drawable.home_stage3, R.drawable.home_stage3, R.drawable.home_stage4, R.drawable.home_stage5, R.drawable.home_stage6, R.drawable.home_stage7)
    private var timer: CountDownTimer? = null
    private var state = 0

    private val refreshRateMs = 500L

    fun start() {
        // find current state of the event progress
        while (state < times.size && times[state].isBeforeNow()) {
            state++
        }
        startTimer()
    }

    private fun startTimer() {
        // if past the last event, don't start another timer
        if (state >= times.size) {
            listener.updateBackground(backgrounds[backgrounds.size - 1])
            return
        }

        // else set the current title and start timer until next timestamp
        listener.updateBackground(backgrounds[state])
        val millisTillTimerFinishes = times[state].timeUntilMs()

        timer = object : CountDownTimer(millisTillTimerFinishes, refreshRateMs) {
            override fun onTick(millisUntilFinished: Long) {
                // do nothing
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
        fun updateBackground(newBackgroundResource: Int)
    }
}
