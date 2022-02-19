package org.hackillinois.android.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.hackillinois.android.database.dao.*
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.database.entity.Leaderboard

@Database(
    entities = [
        QR::class,
        Attendee::class,
        User::class,
        Event::class,
        Roles::class,
        Project::class,
        Profile::class,
        Leaderboard::class
    ],
    version = 4
)

abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun rolesDao(): RolesDao
    abstract fun projectsDao(): ProjectsDao
    abstract fun profileDao(): ProfileDao
    abstract fun leaderboardDao(): LeaderboardDao
}
