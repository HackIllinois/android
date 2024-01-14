package org.hackillinois.android.view.schedule

data class ShiftListItem(val timeString: String) : ScheduleListItem {
    override fun getType() = 2
}
