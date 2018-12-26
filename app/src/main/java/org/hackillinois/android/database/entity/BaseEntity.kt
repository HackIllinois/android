package org.hackillinois.android.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity

@Entity
abstract class BaseEntity {
    @ColumnInfo(name = "last_refreshed") var lastRefreshed: Long = 0L
}
