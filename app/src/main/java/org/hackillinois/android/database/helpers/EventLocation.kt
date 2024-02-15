package org.hackillinois.android.database.helpers

data class EventLocation(
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val tags: List<String> = listOf(),
)
