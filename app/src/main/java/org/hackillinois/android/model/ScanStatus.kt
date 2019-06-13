package org.hackillinois.android.model

data class ScanStatus(val lastScanWasSuccessful: Boolean, var userId: String, val userMessage: String)