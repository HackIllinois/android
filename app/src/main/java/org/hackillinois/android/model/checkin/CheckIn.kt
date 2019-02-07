package org.hackillinois.android.model.CheckIn

data class CheckIn(val id: String, val override: Boolean, val hasCheckedIn: Boolean,
                   val hasPickedUpSwag: Boolean)