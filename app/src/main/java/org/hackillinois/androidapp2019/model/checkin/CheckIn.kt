package org.hackillinois.androidapp2019.model.checkin

data class CheckIn(
        val id: String,
        val override: Boolean,
        val hasCheckedIn: Boolean,
        val hasPickedUpSwag: Boolean
)