package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.hackillinois.android.database.Converters
import java.util.*

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class Event(
    @PrimaryKey val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>
) : BaseEntity() {
    
    fun getStartTimeMs() = startTime * 1000L
    fun getStartTimeOfDay(): String {
        val eventStartTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
            timeInMillis = startTime * 1000L
        }
        val hour = eventStartTime.get(Calendar.HOUR_OF_DAY)
        val minutes = eventStartTime.get(Calendar.MINUTE)

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
}

data class EventLocation (
    val description: String,
    val latitude: Double,
    val longitude: Double
)
