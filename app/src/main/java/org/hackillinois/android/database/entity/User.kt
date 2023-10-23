package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    var userId: String,
    var name: String,
    var email: String,
) {
    @PrimaryKey
    var key = 1
}
