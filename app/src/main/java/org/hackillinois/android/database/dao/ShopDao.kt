package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import org.hackillinois.android.database.entity.ShopItem

@Dao
interface ShopDao {

    @Query("SELECT * FROM shop")
    fun getShop(): LiveData<List<ShopItem>>

    @Query("DELETE FROM shop")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<ShopItem>)

    @Transaction
    fun clearTableAndInsertShopItems(items: List<ShopItem>) {
        clearTable()
        insertAll(items)
    }
}
