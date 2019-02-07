package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class AttendeeRepository {
    private val attendeeDao = App.getDatabase().attendeeDao()

    fun fetchAttendee(): LiveData<Attendee> {
        refreshAttendee()
        return attendeeDao.getAttendee()
    }

    private fun refreshAttendee() {
        App.getAPI().attendee.enqueue(object : Callback<Attendee> {
            override fun onResponse(call: Call<Attendee>, response: Response<Attendee>) {
                if (response.isSuccessful) {
                    val attendee = response.body()
                    thread {
                        attendee?.let { attendeeDao.insert(it) }
                    }
                }
            }

            override fun onFailure(call: Call<Attendee>, t: Throwable) { }
        })
    }

    companion object {
        val instance = AttendeeRepository()
    }
}