package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import kotlin.concurrent.thread

class AttendeeRepository {
    private val attendeeDao = App.getDatabase().attendeeDao()

    fun fetchAttendee(): LiveData<Attendee> {
        refreshAttendee()
        return attendeeDao.getAttendee()
    }

    private fun refreshAttendee() {
        thread {
            try {
                val response = App.getAPI().attendee.execute()
                response?.let {
                    if (response.isSuccessful) {
                        val attendee = it.body()
                        attendee?.let {
                            attendeeDao.insert(attendee)
                        }
                    }
                }
            } catch (exception: Exception) {
                Log.e("AttendeeRepository", exception.message)
            }
        }
    }

    companion object {
        val instance = AttendeeRepository()
    }
}