package org.hackillinois.android.model.scanner

data class ScanStatus(
    val lastScanWasSuccessful: Boolean,
    var points: Int,
    val userMessage: String,
    val dietary: String? = null,
)
