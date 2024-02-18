package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.ShopRepository
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.rolesRepository
import org.hackillinois.android.repository.userRepository

class MainViewModel : ViewModel() {
    lateinit var roles: LiveData<Roles>
    lateinit var attendee: LiveData<Attendee>
    lateinit var user: LiveData<User>

    private val shopRepository = ShopRepository.instance
    private val profileRepository = ProfileRepository.instance

    fun init() {
        attendee = attendeeRepository.fetch()
        user = userRepository.fetch()
        roles = rolesRepository.fetch()

        // fetch shop, schedule, and profile initially to reduce UI lag
        shopRepository.fetchShop()
        profileRepository.fetchProfile()
    }
}
