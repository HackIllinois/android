package org.hackillinois.android.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.hackillinois.android.database.Converters

@Entity(tableName = "roles")
@TypeConverters(Converters::class)
data class Roles(
    @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "roles") var roles: List<String>
) {
    @PrimaryKey
    @ColumnInfo(name = "key")
    var key = 1
}
