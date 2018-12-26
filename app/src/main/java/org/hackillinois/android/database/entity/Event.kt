package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double
) : BaseEntity() {
    fun getStart(): Calendar = Calendar.getInstance().apply { timeInMillis = startTime * MILLIS_IN_SECONDS }
    fun getEnd(): Calendar = Calendar.getInstance().apply { timeInMillis = endTime * MILLIS_IN_SECONDS }
}

private const val MILLIS_IN_SECONDS = 1000L
