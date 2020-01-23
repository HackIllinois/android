package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters


@Entity(tableName = "projects")
@TypeConverters(Converters::class)
data class ProjectModel(
        @PrimaryKey val tag: String,
        val name: String,
        val number: Int,
        val location: String,
        val description: String
)