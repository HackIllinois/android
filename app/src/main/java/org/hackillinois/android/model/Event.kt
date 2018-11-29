package org.hackillinois.android.model

import java.util.*

data class Event(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String
) {

    private val MILLIS_IN_SECONDS = 1000

    fun getStart(): Calendar = Calendar.getInstance().apply { timeInMillis = startTime * MILLIS_IN_SECONDS }
    fun getEnd(): Calendar = Calendar.getInstance().apply { timeInMillis = endTime * MILLIS_IN_SECONDS }
}
