package org.hackillinois.android.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "roles")
@TypeConverters(Converters::class)
data class Roles(
    var id: String,
    var roles: List<String>
) {
    @PrimaryKey
    var key = 1

    fun isStaff() = roles.contains("STAFF")

    fun isPro() = roles.contains("PRO")
}
