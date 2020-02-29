package org.hackillinois.android.model

data class TimesWrapper(
    val id: String,
    val data: Times
)

data class Times(
    val eventEnd: Long,
    val eventStart: Long,
    val fridayEnd: Long,
    val fridayStart: Long,
    val hackEnd: Long,
    val hackStart: Long,
    val saturdayEnd: Long,
    val saturdayStart: Long,
    val sundayEnd: Long,
    val sundayStart: Long
)