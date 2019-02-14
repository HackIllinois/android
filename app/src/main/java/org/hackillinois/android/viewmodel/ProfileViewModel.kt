package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.qrRepository
import org.hackillinois.android.repository.userRepository

class ProfileViewModel : ViewModel() {
    lateinit var qr: LiveData<QR>
    lateinit var attendee: LiveData<Attendee>
    lateinit var user: LiveData<User>

    fun init() {
        qr = qrRepository.fetch()
        attendee = attendeeRepository.fetch()
        user = userRepository.fetch()
    }
}
