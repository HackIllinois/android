package org.hackillinois.android.model.scanner

data class ScanStatus(
    var points: Int,
    val userMessage: String,
    val dietary: String? = null,
)
