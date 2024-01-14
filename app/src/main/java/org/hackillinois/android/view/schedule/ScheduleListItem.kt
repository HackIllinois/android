package org.hackillinois.android.view.schedule

interface ScheduleListItem {
    fun getType(): Int
    fun getStartTimeMs() = 0L
    fun getStartTimeOfDay() = ""
    fun getEndTimeOfDay() = ""
}
