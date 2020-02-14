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
    var lastName: String
) {
    @PrimaryKey
    var key = 1

    val fullName: String
        get() = when {
            firstName.isNotEmpty() || lastName.isNotEmpty() -> "$firstName $lastName".trim()
            else -> "Attendee"
        }
}
