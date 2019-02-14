package org.hackillinois.android2019.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import org.hackillinois.android2019.database.dao.*
import org.hackillinois.android2019.database.entity.*

@Database(entities = [QR::class, Attendee::class, User::class, Event::class, Roles::class], version = 7)
abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun rolesDao(): RolesDao
}
