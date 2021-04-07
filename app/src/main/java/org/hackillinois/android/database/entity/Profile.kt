package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Profile(
    @PrimaryKey var id: String,
    var firstName: String,
    var lastName: String,
    var points: Int,
    var timezone: String,
    var avatarUrl: String,
    var discord: String,
    var teamStatus: String,
    var description: String,
    var interests: List<String>
)