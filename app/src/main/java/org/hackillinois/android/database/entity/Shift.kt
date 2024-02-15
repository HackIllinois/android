package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters
import org.hackillinois.android.database.helpers.EventLocation
import org.hackillinois.android.view.schedule.ScheduleListItem
import java.util.Calendar
import java.util.TimeZone

@Entity(tableName = "shifts")
@TypeConverters(Converters::class)
data class Shift(
    @PrimaryKey val eventId: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val isAsync: Boolean = false
) : ScheduleListItem {

    override fun getStartTimeMs() = startTime * 1000L

    override fun getStartTimeOfDay(): String {
        return getTimeOfDay(startTime)
    }

    override fun getEndTimeOfDay(): String {
        return getTimeOfDay(endTime)
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
    fun isCurrentlyHappening(): Boolean {
        val currentTime = System.currentTimeMillis()
        return startTime * 1000L <= currentTime && currentTime <= endTime * 1000L
    }
    override fun getType() = 2
}
