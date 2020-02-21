package org.hackillinois.android.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QR(
    @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "qr_info") var qrInfo: String
) {
    @PrimaryKey
    @ColumnInfo(name = "key")
    var key = 1
}
