package org.hackillinois.android.view.schedule

data class TimeListItem(val timeString: String) : ScheduleListItem {
    override fun getType() = 2
}