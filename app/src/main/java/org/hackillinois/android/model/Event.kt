package org.hackillinois.android.model

data class Event(
    val name: String,
    val description: String,
    val startTime: Long,
    val endTime: Long,
    val locationDescription: String
)