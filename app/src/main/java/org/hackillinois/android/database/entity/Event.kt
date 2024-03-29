package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters
import org.hackillinois.android.database.helpers.EventLocation
import org.hackillinois.android.view.schedule.ScheduleListItem
import java.util.*

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class Event(
    @PrimaryKey val eventId: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val sponsor: String?,
    val eventType: String,
    val points: String,
    val isAsync: Boolean = false,
    val isPrivate: Boolean,
    val displayOnStaffCheckIn: Boolean,
    val mapImageUrl: String?,
    val isPro: Boolean
) : ScheduleListItem {

    override fun getStartTimeMs() = startTime * 1000L

    override fun getStartTimeOfDay(): String {
        return getTimeOfDay(startTime)
    }

    override fun getEndTimeOfDay(): String {
        return getTimeOfDay(endTime)
    }

    fun isCurrentlyHappening(): Boolean {
        val currentTime = System.currentTimeMillis()
        return startTime * 1000L <= currentTime && currentTime <= endTime * 1000L
    }

    private fun getTimeOfDay(time: Long): String {
        val eventEndTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = time * 1000L
        }
        val hour = eventEndTime.get(Calendar.HOUR_OF_DAY)
        val minutes = eventEndTime.get(Calendar.MINUTE)

        return when {
            hour == 0 -> String.format("12:%02d AM", minutes)
            hour < 12 -> String.format("%d:%02d AM", hour, minutes)
            hour == 12 -> String.format("12:%02d PM", minutes)
            else -> String.format("%d:%02d PM", hour % 12, minutes)
        }
    }

    fun getLocationDescriptionsAsString(): String? {
        if (locations.isEmpty()) {
            return null
        }

        var list = locations[0].description
        if (locations.size > 2) {
            list += ","
        }
        for (i in 1 until locations.size - 1) {
            list += String.format(" %s,", locations[i].description)
        }
        if (locations.size > 1) {
            list += String.format(" and %s", locations[locations.size - 1].description)
        }
        return list
    }

    override fun getType() = 1
}
