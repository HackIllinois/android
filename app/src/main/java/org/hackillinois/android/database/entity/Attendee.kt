package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "attendees")
data class Attendee(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var diet: String,
    var school: String,
    var major: String
): BaseEntity() {
    // again makes the Attendee a singleton in the database
    // TODO: fix like QR on login
    @PrimaryKey var key = 1

    fun getFullName() = String.format("%s %s", firstName, lastName)
}
