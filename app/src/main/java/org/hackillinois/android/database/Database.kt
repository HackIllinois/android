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
        Profile::class,
        Leaderboard::class,
        Shift::class,
        ShopItem::class,
    ],
    version = 3,
)

abstract class Database : RoomDatabase() {
    abstract fun qrDao(): QRDao
    abstract fun attendeeDao(): AttendeeDao
    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao
    abstract fun rolesDao(): RolesDao
    abstract fun profileDao(): ProfileDao
    abstract fun leaderboardDao(): LeaderboardDao
    abstract fun shiftDao(): ShiftDao
    abstract fun shopDao(): ShopDao
}
