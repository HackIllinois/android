package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.repository.AttendeeRepository

class MainViewModel : ViewModel() {

    private val attendeeRepository = AttendeeRepository.instance
    lateinit var attendee: LiveData<Attendee>

    fun init() {
        attendee = attendeeRepository.fetchAttendee()
    }
}
