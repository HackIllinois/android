package org.hackillinois.android.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.hackillinois.android.database.dao.QRDao
import org.hackillinois.android.database.entity.QR

@Database(entities = [QR::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
}
