package org.hackillinois.android.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.android.database.entity.QR

@Dao
interface QRDao {
    @Query("SELECT * FROM qr_codes LIMIT 1")
    fun getQr(): LiveData<QR>

    @Query("SELECT * FROM qr_codes WHERE last_refreshed > :lastRefreshMax LIMIT 1")
    fun hasUpdatedQr(lastRefreshMax: Long): QR?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qr: QR)

    @Delete
    fun delete(qr: QR)
}
