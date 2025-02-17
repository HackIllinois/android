package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class Cart(
    @PrimaryKey val userId: String,
    val items: Map<String, Int> // Maps itemId to quantity
)
