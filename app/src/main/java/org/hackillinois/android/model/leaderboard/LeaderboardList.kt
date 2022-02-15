package org.hackillinois.android.model.leaderboard

import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@TypeConverters(Converters::class)
data class LeaderboardList(
    @PrimaryKey var profiles: List<LeaderboardEntity>
)
