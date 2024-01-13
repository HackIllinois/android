package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_point_shop.view.*
// import kotlinx.android.synthetic.main.fragment_point_shop.view.coin_total_textview
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.ShopRepository

class ShopViewModel : ViewModel() {
    private val shopRepository = ShopRepository.instance
    private val profileRepository = ProfileRepository.instance

    lateinit var shopLiveData: LiveData<List<ShopItem>>
    lateinit var profileLiveData: LiveData<Profile>

    var coinTotal: Int = 0
    fun init() {
        shopLiveData = shopRepository.fetchShop()
        profileLiveData = profileRepository.fetchProfile()
        coinTotal = profileLiveData.value?.coins!!
    }
}
