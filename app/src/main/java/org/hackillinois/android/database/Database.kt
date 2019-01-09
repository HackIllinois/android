package org.hackillinois.android.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.hackillinois.android.database.dao.AttendeeDao
import org.hackillinois.android.database.dao.EventDao
import org.hackillinois.android.database.dao.QRDao
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.QR

@Database(entities = [QR::class, Attendee::class, Event::class], version = 3)
abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun eventDao(): EventDao
}
