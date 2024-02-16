package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.ShopItem
import java.lang.Exception

class ShopRepository {
    private val shopDao = App.database.shopDao()

    fun fetchShop(): LiveData<List<ShopItem>> {
        // 'refreshAll()' coroutine is called only when shop button on bottom app bar clicked
        refreshAll()
        // locally stored database
        val lb = shopDao.getShop()
        return lb
    }

    fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val shop = App.getAPI().shop()
                Log.d("SHOP REFRESH ALL", shop.toString())
                shopDao.clearTableAndInsertShopItems(shop)
            } catch (e: Exception) {
                Log.e("SHOP REFRESH ALL", e.toString())
            }
        }
    }

    companion object {
        val instance: ShopRepository by lazy { ShopRepository() }
    }
}
