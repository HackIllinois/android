package org.hackillinois.android.model.scanner

data class ScanStatus(
    var points: Int,
    var userMessage: String,
    var dietary: String? = null,
    var success: Boolean
)
