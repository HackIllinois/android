package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Profile(
    var _id: String,
    var displayName: String,
    var discordTag: String,
    var avatarUrl: String,
    var points: Int,
    var userId: String,
    var foodWave: Int,
    // var ranking: Int,
) {
    @PrimaryKey
    var key = 1
}
