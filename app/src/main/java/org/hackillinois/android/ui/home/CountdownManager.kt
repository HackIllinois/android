package org.hackillinois.android.ui.home

import android.os.CountDownTimer
import android.util.Log
import org.hackillinois.android.utils.TimeInfo
import java.util.*

class CountdownManager(val listener: CountDownListener, val times: List<Calendar>) {

    private var timer: CountDownTimer? = null
    private var index = 0

    fun start() {
        while (index < times.size && times[index].timeInMillis < Calendar.getInstance().timeInMillis) {
            index++
        }
        startTimer()
    }

    private fun startTimer() {
        if (index >= times.size) {
            listener.updateTimer(index)
            return
        }
        listener.updateTimer(index)

        val millisTillTimerFinishes = times[index].timeInMillis - Calendar.getInstance().timeInMillis
        timer = object : CountDownTimer(millisTillTimerFinishes, 500) {
            override fun onTick(millisUntilFinished: Long) {
                val diff = times[index].timeInMillis - Calendar.getInstance().timeInMillis
                val diffTimeInfo = TimeInfo(diff)
                listener.updateTime(diffTimeInfo)
            }

            override fun onFinish() {
                index++
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
        fun updateTime(timeInfo: TimeInfo)
        fun updateTimer(index: Int)
    }
}
