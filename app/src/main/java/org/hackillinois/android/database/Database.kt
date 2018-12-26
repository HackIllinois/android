package org.hackillinois.android.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.hackillinois.android.database.dao.AttendeeDao
import org.hackillinois.android.database.dao.QRDao
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR

@Database(entities = [QR::class, Attendee::class], version = 2)
abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
}
