package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Profile(
    var _id: String,
    var userId: String,
    var avatarUrl: String,
    var discordTag: String,
    var displayName: String,
    var foodWave: Int,
    var points: Int,
    var pointsAccumulated: Int
) {
    @PrimaryKey
    var key = 1
}
