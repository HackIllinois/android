package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.repository.ShopRepository

class ShopViewModel : ViewModel() {
    private val shopRepository = ShopRepository.instance

    lateinit var shopLiveData: LiveData<List<ShopItem>>

    fun init() {
        shopLiveData = shopRepository.fetchShop()
    }
}
