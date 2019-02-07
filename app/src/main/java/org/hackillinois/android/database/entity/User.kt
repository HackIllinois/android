package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "users")
data class User(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var username: String
): BaseEntity() {
    // again makes the User a singleton in the database
    // TODO: fix like QR on login
    @PrimaryKey var key = 1

    fun getFullName() = String.format("%s %s", firstName, lastName)
}
