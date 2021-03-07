package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.R
import org.hackillinois.android.database.Converters

@Entity(tableName = "profiles")
@TypeConverters(Converters::class)
data class Profile(
    @PrimaryKey val id: String,
    val firstName: String,
    val lastName: String,
    val points: Int,
    val timezone: String,
    val avatarUrl: String,
    val discord: String,
    val teamStatus: String,
    val description: String,
    val interests: List<String>
)