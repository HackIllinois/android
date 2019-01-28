package org.hackillinois.android.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import org.hackillinois.android.database.entity.Event
import android.arch.persistence.room.Transaction

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE name LIKE :name LIMIT 1")
    fun getEvent(name: String): LiveData<Event>

    @Query("SELECT * FROM events WHERE startTime <= :time AND endTime >= :time")
    fun getAllEventsHappeningAtTime(time: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE startTime >= :startTime AND startTime < :endTime ORDER BY startTime")
    fun getEventsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE last_refreshed > :lastRefreshMax AND name LIKE :name LIMIT 1")
    fun hasUpdatedEvent(name: String, lastRefreshMax: Long): Event?

    @Query("SELECT MIN(last_refreshed) FROM events")
    fun getTimeOfOldestRefreshedEvent(): Long?

    @Query("DELETE FROM events")
    fun clearTable()

    @Transaction
    fun clearTableAndInsertEvents(events: List<Event>) {
        clearTable()
        insertAll(events)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(events: List<Event>)

    @Delete
    fun delete(event: Event)
}
