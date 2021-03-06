package org.hackillinois.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.hackillinois.android.database.dao.*
import org.hackillinois.android.database.entity.*

@Database(
    entities = [
        QR::class,
        Attendee::class,
        User::class,
        Event::class,
        Roles::class,
        Project::class,
        Profile::class
    ],
    version = 2
)
abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun rolesDao(): RolesDao
    abstract fun projectsDao(): ProjectsDao
    abstract fun profileDao(): ProfileDao
}
