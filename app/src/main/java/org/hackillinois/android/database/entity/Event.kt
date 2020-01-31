package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.R
import org.hackillinois.android.database.Converters
import org.hackillinois.android.view.schedule.ScheduleListItem
import java.util.*

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class Event(
    @PrimaryKey val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val sponsor: String,
    val eventType: String
) : ScheduleListItem {

    fun getStartTimeMs() = startTime * 1000L

    fun getStartTimeOfDay(): String {
        return getTimeOfDay(startTime)
    }

    fun getEndTimeOfDay(): String {
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
            hour == 0 -> String.format("12:%02dam", minutes)
            hour < 12 -> String.format("%d:%02dam", hour, minutes)
            hour == 12 -> String.format("12:%02dpm", minutes)
            else -> String.format("%d:%02dpm", hour % 12, minutes)
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

    // TODO: implement this once API is updated
    fun getIndoorMapAndDirectionInfo(): List<IndoorMapAndDirectionInfo> {
        return listOf(
            IndoorMapAndDirectionInfo(
                "Siebel 1214",
                R.drawable.dcl,
                LatLng(0.0, 0.0)
            )
        )
    }

    override fun getType() = 1
}

data class EventLocation(
    val description: String,
    val latitude: Double,
    val longitude: Double
)

data class IndoorMapAndDirectionInfo(
    val locationDescription: String,
    val indoorMapResource: Int,
    val latLng: LatLng
)

val SiebelCenter = EventLocation("Siebel Center", 40.1138, -88.2249)
val EceBuilding = EventLocation("ECE Building", 40.1148, -88.2280)
