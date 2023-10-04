package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.database.Converters
import org.hackillinois.android.view.schedule.ScheduleListItem
import java.util.*

@Entity(tableName = "events")
@TypeConverters(Converters::class)
data class Event(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val sponsor: String,
    val eventType: String,
    val points: String,
    val isAsync: Boolean = false
) : ScheduleListItem {

    fun getStartTimeMs() = startTime * 1000L

    fun getStartTimeOfDay(): String {
        return getTimeOfDay(startTime)
    }

    fun getEndTimeOfDay(): String {
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

    override fun getType() = 1
}

data class EventLocation(
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String> = listOf()
)

data class IndoorMapAndDirectionInfo(
    val locationDescription: String,
    val indoorMapResource: Int,
    val latLng: LatLng
)

data class EventCode(val code: String)

data class MeetingEventId(val eventId: String)

data class MeetingCheckInResponse(var status: String)

data class EventCheckInResponse(
    val newPoints: Int,
    val totalPoints: Int,
    val status: String
)

data class EventCheckInAsStaffResponse(
    val newPoints: Int,
    val totalPoints: Int,
    val status: String,
    val rsvpData: RSVPData
)

data class RSVPData(
    val id: String,
    val isAttending: Boolean,
    val registrationData: RegistrationData
)

data class RegistrationData(
    val attendee: AttendeeData
)

data class AttendeeData(
    val dietary: List<String>
)
