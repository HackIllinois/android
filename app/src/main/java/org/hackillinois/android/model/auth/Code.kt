package org.hackillinois.android.model.auth

data class Code(val code: String)

data class EventCheckinResponse(val newPoints: Int, val totalPoints: Int, val status: String)