package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class ProjectModel(
    val id: String,
    val name: String,
    val description: String,
    val mentors: String,
    val room: String,
    @PrimaryKey val tags: String,
    val number: Int
)