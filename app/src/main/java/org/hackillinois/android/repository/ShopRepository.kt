package org.hackillinois.android.repository

import org.hackillinois.android.App

class ShopRepository {
    private val leaderboardDao = App.database.shopDao()

    // write fetchShop() and refreshAll() functions
    // look at other repository functions for reference (i.e. LeaderboardRepository)

    companion object {
        val instance: ShopRepository by lazy { ShopRepository() }
    }
}
