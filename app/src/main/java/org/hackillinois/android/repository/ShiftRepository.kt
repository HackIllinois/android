package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Shift
import org.hackillinois.android.database.helpers.EventLocation
import org.hackillinois.android.model.event.ShiftsList

class ShiftRepository {
    private val shiftDao = App.database.shiftDao()

    fun fetchShiftsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Shift>> {
        return shiftDao.getShiftsHappeningBetweenTimes(startTime / MILLIS_IN_SECOND, endTime / MILLIS_IN_SECOND)
    }

    suspend fun refreshAllShifts() {
        // ensures database operation happens on the IO dispatcher
        withContext(Dispatchers.IO) {
            try {
                 val shifts = App.getAPI().allShifts().shifts
                Log.d("Shifts Fetched", shifts.toString())
                shiftDao.clearTableAndInsertShifts(shifts)
            } catch (e: Exception) {
                Log.e("REFRESH SHIFTS", e.toString())
            }
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
        val instance: ShiftRepository by lazy { ShiftRepository() }
    }
}
