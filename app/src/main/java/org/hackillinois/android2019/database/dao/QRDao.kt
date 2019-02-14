package org.hackillinois.android2019.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.android2019.database.entity.QR

@Dao
interface QRDao {
    @Query("SELECT * FROM qr_codes LIMIT 1")
    fun getQr(): LiveData<QR>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(qr: QR)

    @Delete
    fun delete(qr: QR)
}
