package org.hackillinois.android.extensions

import java.util.*

fun Calendar.timeUntilMs() = (this.timeInMillis - Calendar.getInstance().timeInMillis)
fun Calendar.isAfterNow() = (this.timeUntilMs() > 0)
fun Calendar.isBeforeNow() = (this.timeUntilMs() < 0)
