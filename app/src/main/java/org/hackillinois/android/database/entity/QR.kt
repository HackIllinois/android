package org.hackillinois.android.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "qr_codes")
data class QR(
    @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "qr_info") var qrInfo: String
) {
    @PrimaryKey
    @ColumnInfo(name = "key")
    var key = 1
}
