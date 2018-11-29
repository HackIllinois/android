package org.hackillinois.android.utils

import java.util.*

class StartTimes {
    companion object {
        val eventStartTime: Calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            set(2019, Calendar.FEBRUARY, 22, 17, 0, 0)
        }
        val hackingStartTime: Calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            set(2019, Calendar.FEBRUARY, 22, 23, 0, 0)
        }
        val hackingEndTime: Calendar = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            set(2019, Calendar.FEBRUARY, 24, 11, 0, 0)
        }
    }
}