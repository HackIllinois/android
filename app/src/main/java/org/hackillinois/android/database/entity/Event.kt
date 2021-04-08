package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.Ignore
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
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val sponsor: String,
    val eventType: String,
    val points: String
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

    fun getIndoorMapAndDirectionInfo() = locations.flatMap {
        it.tags.map { tag -> getMapAndDirectionForTag(tag) }
    }

    @Ignore private val ecebLatLng = LatLng(40.115071, -88.228196)
    @Ignore private val siebelLatLng = LatLng(40.113833, -88.224903)
    @Ignore private val kenneyLatLng = LatLng(40.1131422, -88.229537)
    @Ignore private val dclLatLng = LatLng(40.1133081, -88.2288307)

    @Ignore
    private val mapAndDirectionInfoMap: Map<String, IndoorMapAndDirectionInfo> = mapOf(
        "KENNEY" to IndoorMapAndDirectionInfo("Kenney", R.drawable.kenney, kenneyLatLng),
        "DCL" to IndoorMapAndDirectionInfo("DCL", R.drawable.dcl, dclLatLng),
        "ECEB1" to IndoorMapAndDirectionInfo("ECEB Floor 1", R.drawable.eceb_floor_1, ecebLatLng),
        "ECEB2" to IndoorMapAndDirectionInfo("ECEB Floor 2", R.drawable.eceb_floor_2, ecebLatLng),
        "ECEB3" to IndoorMapAndDirectionInfo("ECEB Floor 3", R.drawable.eceb_floor_3, ecebLatLng),
        "SIEBEL0" to IndoorMapAndDirectionInfo("Siebel Basement", R.drawable.siebel_floor_0, siebelLatLng),
        "SIEBEL1" to IndoorMapAndDirectionInfo("Siebel Floor 1", R.drawable.siebel_floor_1, siebelLatLng),
        "SIEBEL2" to IndoorMapAndDirectionInfo("Siebel Floor 2", R.drawable.siebel_floor_2, siebelLatLng)
    )

    private fun getMapAndDirectionForTag(tag: String) = mapAndDirectionInfoMap[tag]
        ?: IndoorMapAndDirectionInfo("Siebel Floor 1", R.drawable.siebel_floor_1, siebelLatLng)

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
