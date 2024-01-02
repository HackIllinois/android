package org.hackillinois.android.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import org.hackillinois.android.database.entity.Shift

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts")
    fun getShifts(): LiveData<Shift>

    @Query("DELETE FROM shifts")
    fun clearTable()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(shifts: List<Shift>)

    @Transaction
    fun clearTableAndInsertShifts(shifts: List<Shift>) {
        clearTable()
        insertAll(shifts)
    }
}
