package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import kotlin.concurrent.thread

class AttendeeRepository {
    private val attendeeDao = App.getDatabase().attendeeDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchAttendee(): LiveData<Attendee> {
        refreshAttendee()
        return attendeeDao.getAttendee()
    }

    private fun refreshAttendee() {
        thread {
            // checks to see if an updated version of the qr code is in the database
            // if not, run API query and save it in DB
            val userExists = attendeeDao.hasUpdatedAttendee(System.currentTimeMillis() - millisTillStale) != null
            if (!userExists) {
                val response = App.getAPI().attendee.execute()
                response?.let {
                    val attendee = it.body()
                    attendee?.let {
                        attendee.lastRefreshed = System.currentTimeMillis()
                        attendeeDao.insert(attendee)
                    }
                }
            }
        }
    }

    companion object {
        val instance = AttendeeRepository()
    }
}