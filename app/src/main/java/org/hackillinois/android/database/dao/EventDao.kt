package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events")
    fun getAllEventsList(): List<Event>

    @Query("SELECT * FROM events WHERE eventId = :eventId LIMIT 1")
    fun getEvent(eventId: String): LiveData<Event>

    @Query("SELECT * FROM events WHERE startTime <= :time AND endTime >= :time AND isAsync = 0")
    fun getAllEventsHappeningAtTime(time: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE startTime >= :startTime AND startTime < :endTime AND isAsync = 0 ORDER BY startTime, name")
    fun getEventsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE startTime > :currentTime AND isAsync = 0 ORDER BY startTime")
    fun getEventsAfter(currentTime: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE isAsync = 1 ORDER BY name")
    fun getAsyncEvents(): LiveData<List<Event>>

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
