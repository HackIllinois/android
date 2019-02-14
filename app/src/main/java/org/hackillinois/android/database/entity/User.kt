package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    var id: String,
    var firstName: String,
    var lastName: String,
    var email: String,
    var username: String
) {
    @PrimaryKey var key = 1

    fun getFullName() = String.format("%s %s", firstName, lastName)
}
