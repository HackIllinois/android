package org.hackillinois.android.view.home

import android.os.CountDownTimer
import org.hackillinois.android.R
import org.hackillinois.android.common.isBeforeNow
import org.hackillinois.android.common.timeUntilMs
import java.util.*

class EventProgressManager(val listener: CountDownListener) {

    // CORRECT TIMES ARE NOT SET YET
    // 02-23-2024 15:30:00
    private val checkInTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708723800000
    }

    // 02-23-2024 16:00:00
    private val scavengerHuntTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708725600000
    }

    // 02-23-2024 18:00:00
    private val openingCeremonyTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708732800000
    }

    // 02-23-2024 19:00:00
    private val hackingTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708736400000
    }

    // 02-25-2024 11:00:00
    private val projectShowcaseTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708880400000
    }

    // 02-25-2024 15:00:00
    private val closingCeremonyTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708894800000
    }

    // 02-25-2024 16:00:00
    private val afterHackathonTime: Calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708898400000
    }

    private var times = listOf(checkInTime, scavengerHuntTime, openingCeremonyTime, hackingTime, projectShowcaseTime, closingCeremonyTime, afterHackathonTime)
    private var backgrounds = listOf(R.drawable.home_bg_start, R.drawable.home_check_in_bg, R.drawable.home_scavenger_hunt_bg, R.drawable.home_opening_bg, R.drawable.home_hacking_bg, R.drawable.home_project_showcase_bg, R.drawable.home_closing_bg, R.drawable.home_final_bg)
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
