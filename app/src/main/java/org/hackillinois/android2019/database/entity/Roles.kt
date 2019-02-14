package org.hackillinois.android2019.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.TypeConverters
import org.hackillinois.android2019.database.Converters

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
