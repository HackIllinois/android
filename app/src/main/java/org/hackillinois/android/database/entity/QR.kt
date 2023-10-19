package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QR(
    var userId: String,
    var qrInfo: String
) {
    @PrimaryKey
    var key = 1
}
