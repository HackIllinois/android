package org.hackillinois.android.model

import org.hackillinois.android.database.entity.User

data class ScanStatus(
    val lastScanWasSuccessful: Boolean,
    var points: Int,
    val userMessage: String,
    val dietary: String? = null,
)
