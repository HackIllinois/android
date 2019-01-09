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
        val builder = StringBuilder()
        val hour = eventStartTime.get(Calendar.HOUR_OF_DAY)
        val minutes = eventStartTime.get(Calendar.MINUTE)

        if (hour > 12) {
            builder.append(hour % 12)
            builder.append(":")
            builder.append(minutes)
            builder.append(" PM")
        } else {
            builder.append(hour)
            builder.append(":")
            builder.append(minutes)
            builder.append(" AM")
        }

        return builder.toString()
    }
}
