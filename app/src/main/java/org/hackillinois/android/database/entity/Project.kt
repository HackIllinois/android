package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.R
import org.hackillinois.android.database.Converters

@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class Project(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val mentors: List<String>,
    val room: String,
    val tags: List<String>,
    val number: Int
) {
    fun getMentorsString(): String {
        var mentors = ""
        for (i in 0 until this.mentors.lastIndex) {
            mentors += this.mentors[i] + ", "
        }
        mentors += this.mentors[this.mentors.lastIndex]

        return mentors
    }

    @Ignore
    private val mapResourceMappings = mapOf(
        "Siebel_0" to R.drawable.siebel_floor_0,
        "Siebel_1" to R.drawable.siebel_floor_1,
        "Siebel_2" to R.drawable.siebel_floor_2,
        "ECEB_1" to R.drawable.eceb_floor_1,
        "ECEB_2" to R.drawable.eceb_floor_2,
        "ECEB_3" to R.drawable.eceb_floor_3,
        "DCL" to R.drawable.dcl,
        "Kenney" to R.drawable.kenney
    )

    fun getBuildingName(): String {
        val split = room.split(" ")
        return if (split.isEmpty()) {
            "No Name"
        } else {
            split[0]
        }
    }

    @Ignore
    private val NO_RESOURCE = 0

    fun getIndoorMapResource(): Int {
        val split = room.split(" ")
        return when {
            split.isEmpty() -> NO_RESOURCE
            split.size == 1 -> {
                val buildingName = split[0]
                mapResourceMappings[buildingName] ?: NO_RESOURCE
            }
            else -> {
                val buildingName = split[0]
                val floor = split.last()[0]
                val name = "${buildingName}_$floor"
                mapResourceMappings[name] ?: NO_RESOURCE
            }
        }
    }

    @Ignore
    private val latLngMappings = mapOf(
        "Siebel" to LatLng(40.113833, -88.224903),
        "ECEB" to LatLng(40.115071, -88.228196),
        "DCL" to LatLng(40.113349, -88.228713),
        "Kenney" to LatLng(40.1132401, -88.2305837)
    )

    fun getLatLng(): LatLng {
        val split = room.split(" ")
        return if (split.isEmpty()) {
            LatLng(40.113833, -88.224903)
        } else {
            latLngMappings[split[0]] ?: LatLng(40.113833, -88.224903)
        }
    }
}
