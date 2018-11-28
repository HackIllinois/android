package org.hackillinois.android.utils

class TimeInfo(val millis: Long) {
    val days: Long
    val hours: Long
    val minutes: Long
    val seconds: Long

    init {
        var leftOver = millis
        days = leftOver / 86400000
        leftOver -= days * 86400000
        hours = leftOver / 3600000
        leftOver -= hours * 3600000
        minutes = leftOver / 60000
        leftOver -= minutes * 60000
        seconds = leftOver / 1000
        leftOver -= seconds * 1000
    }

}