package org.hackillinois.android.model.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "leaderboard")
@TypeConverters(Converters::class)
data class LeaderboardProfile(
    @PrimaryKey var id: String,
    var points: Int,
    var discord: String
)