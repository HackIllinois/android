package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.repository.AttendeeRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    val attendeeRepository = AttendeeRepository.instance
    lateinit var attendee: LiveData<Attendee>

    fun init() {
        attendee = attendeeRepository.fetchAttendee()
    }
}
