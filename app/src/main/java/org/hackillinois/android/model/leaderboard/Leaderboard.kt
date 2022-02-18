package org.hackillinois.android.model.leaderboard

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Leaderboard(
    @PrimaryKey var id: String,
    var firstName: String,
    var lastName: String,
    var points: Int
)