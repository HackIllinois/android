package org.hackillinois.android.database.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String,
    val latitude: Double,
    val longitude: Double
) : BaseEntity()
