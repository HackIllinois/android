package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.ShopRepository
import org.hackillinois.android.repository.userRepository

class MainViewModel : ViewModel() {
    lateinit var user: LiveData<User>

    private val shopRepository = ShopRepository.instance
    private val profileRepository = ProfileRepository.instance

    fun init() {
        user = userRepository.fetch()

        // fetch shop, schedule, and profile initially to reduce UI lag
        shopRepository.fetchShop()
        profileRepository.fetchProfile()
    }
}
