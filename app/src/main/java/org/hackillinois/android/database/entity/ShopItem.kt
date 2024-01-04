package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "shop")
@TypeConverters(Converters::class)
data class ShopItem(
    @PrimaryKey val itemId: String,
    var name: String,
    var price: Int,
    var isRaffle: Boolean,
    var quantity: Int
)
