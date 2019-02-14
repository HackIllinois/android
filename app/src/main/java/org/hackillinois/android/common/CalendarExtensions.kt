package org.hackillinois.android.common

import java.util.*

fun Calendar.timeUntilMs() = (this.timeInMillis - Calendar.getInstance().timeInMillis)
fun Calendar.isBeforeNow() = (this.timeUntilMs() < 0)
