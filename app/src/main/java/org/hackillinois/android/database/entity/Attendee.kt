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
) {
    @PrimaryKey
    var key = 1

    val fullName: String
        get() = when {
            firstName.isNotEmpty() || lastName.isNotEmpty() -> "$firstName $lastName".trim()
            else -> "Attendee"
        }

    val completeDiet: String?
        get() =
            if (diet.isEmpty()) null
            else buildString {
                append(diet.first())

                if (diet.size > 2) {
                    diet.subList(1, diet.size - 1).forEach {
                        append(", $it")
                    }
                }

                if (diet.size > 1) {
                    append(" and ${diet.last()}")
                }
            }
}
