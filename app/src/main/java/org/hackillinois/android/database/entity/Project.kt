package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
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

    fun getBuildingName(): String {
        val split = room.split(" ")
        return if (split.isNotEmpty()) {
            split[0]
        } else {
            ""
        }
    }

    @Ignore
    private val NO_RESOURCE = 0

//    fun getIndoorMapResource(): Int? {
//        val split = room.split(" ")
//        if (split.isEmpty()) {
//            return NO_RESOURCE
//        }
//
//        val buildingName = split[0]
//
//        return if (buildingName == "Siebel") {
//            if (split.size == 1) { R.drawable.siebel_floor_1 }
//
//            val floor = split.last()[0]
//            when (floor) {
//                '0' -> R.drawable.siebel_floor_0
//                '2' -> R.drawable.siebel_floor_2
//                else -> R.drawable.siebel_floor_1
//            }
//        } else if (buildingName == "ECEB") {
//            if (split.size == 1) { R.drawable.eceb_floor_1 }
//
//            val floor = split.last()[0]
//            when (floor) {
//                '2' -> R.drawable.eceb_floor_2
//                '3' -> R.drawable.eceb_floor_3
//                else -> R.drawable.eceb_floor_1
//            }
//        } else {
//            null
//        }
//    }

    @Ignore
    private val latLngMappings = mapOf(
        "Siebel" to LatLng(40.113833, -88.224903),
        "ECEB" to LatLng(40.115071, -88.228196)
    )

    @Ignore
    private val SIEBEL_LAT_LNG = LatLng(40.113833, -88.224903)

    fun getLatLng(): LatLng {
        val split = room.split(" ")
        return if (split.isNotEmpty()) {
            latLngMappings[split[0]] ?: SIEBEL_LAT_LNG
        } else {
            SIEBEL_LAT_LNG
        }
    }
}
