package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.ShopRepository

class ShopViewModel : ViewModel() {
    private val shopRepository = ShopRepository.instance
    private val profileRepository = ProfileRepository.instance

    lateinit var shopLiveData: LiveData<List<ShopItem>>
    lateinit var profileLiveData: LiveData<Profile>

    fun init() {
        shopLiveData = shopRepository.fetchShop()
        profileLiveData = profileRepository.fetchProfile()
    }
}
