package org.hackillinois.android.ui.home

import android.os.CountDownTimer
import android.util.Log
import org.hackillinois.android.utils.TimeInfo
import java.util.*

class CountdownManager(val listener: CountDownListener, val time: Calendar) {

    private lateinit var timer: CountDownTimer

    fun startTimer() {
        val millisTillTimerFinishes = time.timeInMillis - Calendar.getInstance().timeInMillis
        timer = object : CountDownTimer(millisTillTimerFinishes, 500) {
            override fun onTick(millisUntilFinished: Long) {
                val diff = time.timeInMillis - Calendar.getInstance().timeInMillis

                val diffTimeInfo = TimeInfo(diff)
                listener.updateTime(diffTimeInfo)
            }

            override fun onFinish() {

            }
        }.start()
    }

    fun onPause() {
        timer.cancel()
    }

    fun onResume() {
        startTimer()
    }

    interface CountDownListener {
        fun updateTime(timeInfo: TimeInfo)
    }

}