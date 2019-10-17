package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "attendees")
@TypeConverters(Converters::class)
data class Attendee(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var diet: List<String>,
    var school: String,
    var major: String
) {
    @PrimaryKey
    var key = 1

    fun getDietAsString(): String? {
        if (diet.isEmpty()) {
            return null
        }

        var list = diet[0]
        if (diet.size > 2) {
            list += ","
        }
        for (i in 1 until diet.size - 1) {
            list += String.format(" %s,", diet[i])
        }
        if (diet.size > 1) {
            list += String.format(" and %s", diet[diet.size - 1])
        }
        return list
    }
}
