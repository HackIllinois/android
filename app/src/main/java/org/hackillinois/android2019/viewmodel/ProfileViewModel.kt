package org.hackillinois.android2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android2019.database.entity.Attendee
import org.hackillinois.android2019.database.entity.QR
import org.hackillinois.android2019.database.entity.User
import org.hackillinois.android2019.repository.attendeeRepository
import org.hackillinois.android2019.repository.qrRepository
import org.hackillinois.android2019.repository.userRepository

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
