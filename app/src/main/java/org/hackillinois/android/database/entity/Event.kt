package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double
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
}
