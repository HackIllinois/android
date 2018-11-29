package org.hackillinois.android.utils

class TimeInfo(millis: Long) {
    val days: Long
    val hours: Long
    val minutes: Long
    val seconds: Long

    private val MILLIS_IN_SECOND = 1000
    private val SECONDS_IN_MINUTE = 60
    private val MINUTES_IN_HOUR = 60
    private val HOURS_IN_DAY = 24

    init {
        var time = millis / MILLIS_IN_SECOND
        seconds = time % SECONDS_IN_MINUTE
        time /= SECONDS_IN_MINUTE

        minutes = time % MINUTES_IN_HOUR
        time /= MINUTES_IN_HOUR

        hours = time % HOURS_IN_DAY
        time /= HOURS_IN_DAY

        days = time
    }

}
