package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hackillinois.android.database.entity.Attendee

@Dao
interface AttendeeDao {
    @Query("SELECT * FROM attendees LIMIT 1")
    fun getAttendee(): LiveData<Attendee>

//    @Query("SELECT dietary FROM attendees")
//    fun getDietaryRestrictions(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attendee: Attendee)

    @Delete
    fun delete(attendee: Attendee)
}
