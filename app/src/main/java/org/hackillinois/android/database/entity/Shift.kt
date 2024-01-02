package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters
import org.hackillinois.android.database.helpers.EventLocation

@Entity(tableName = "shifts")
@TypeConverters(Converters::class)
data class Shift(
    @PrimaryKey val eventId: String,
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locations: List<EventLocation>,
    val isAsync: Boolean = false,
)
