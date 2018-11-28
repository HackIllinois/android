package org.hackillinois.android.ui.home

import android.os.CountDownTimer
import android.util.Log
import org.hackillinois.android.utils.TimeInfo
import java.util.*

class CountdownManager(val listener: CountDownListener) {

    fun startTimer(time: Calendar) {
        Log.d("CountdownManager", "Starting")
        val millisTillTimerFinishes = time.timeInMillis - Calendar.getInstance().timeInMillis
        Log.d("CountdownManager", millisTillTimerFinishes.toString())
        val countDownTimer = object : CountDownTimer(millisTillTimerFinishes, 500) {
            override fun onTick(millisUntilFinished: Long) {
                val diff = time.timeInMillis - Calendar.getInstance().timeInMillis

                val diffTimeInfo = TimeInfo(diff)
                listener.updateTime(diffTimeInfo)
            }

            override fun onFinish() {

            }
        }.start()
    }

    interface CountDownListener {
        fun updateTime(timeInfo: TimeInfo)
    }

}