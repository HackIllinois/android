package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "leaderboard")
@TypeConverters(Converters::class)
data class Leaderboard(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var displayName: String,
    var points: Int
)
