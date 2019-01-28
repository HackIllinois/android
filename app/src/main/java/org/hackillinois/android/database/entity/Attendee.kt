package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
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
): BaseEntity() {
    // again makes the Attendee a singleton in the database
    // TODO: fix like QR on login
    @PrimaryKey var key = 1

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
