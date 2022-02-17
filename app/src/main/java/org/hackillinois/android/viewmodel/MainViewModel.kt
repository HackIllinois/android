package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.userRepository

class MainViewModel : ViewModel() {
    lateinit var attendee: LiveData<Attendee>
    lateinit var user: LiveData<User>

    fun init() {
        attendee = attendeeRepository.fetch()
        user = userRepository.fetch()
    }
}
