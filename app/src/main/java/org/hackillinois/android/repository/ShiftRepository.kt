package org.hackillinois.android.repository

import android.location.Location
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

    // write fetchShifts() and refreshAll() functions
    // look at other repository functions for reference (i.e. LeaderboardRepository)

    fun fetchShiftsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Shift>> {
        return shiftDao.getShiftsHappeningBetweenTimes(startTime / MILLIS_IN_SECOND, endTime / MILLIS_IN_SECOND)
    }

    suspend fun refreshAllShifts() {
        // ensures database operation happens on the IO dispatcher
        withContext(Dispatchers.IO) {
            try {
                // val shifts = App.getAPI().allShifts().shifts

                val shifts = ShiftsList(
                    listOf(
                        Shift(
                            eventId = "event 1",
                            name = "test 1",
                            description = "...",
                            startTime = 1708734216,
                            endTime = 1708907016,
                            locations = listOf(
                                EventLocation(
                                    description = "SIEBEL",
                                    tags = listOf("TAG1", "TAG2"),
                                    latitude = 12.0,
                                    longitude = -80.0
                                )
                            ),
                            isAsync = false
                        ),
                        Shift(
                            eventId = "event 2",
                            name = "test 2",
                            description = "...",
                            startTime = 1708734216,
                            endTime = 1708907016,
                            locations = listOf(
                                EventLocation(
                                    description = "SIEBEL",
                                    tags = listOf("TAG1", "TAG2"),
                                    latitude = 12.0,
                                    longitude = -80.0
                                )
                            ),
                            isAsync = false
                        ),
                        Shift(
                            eventId = "event 3",
                            name = "test 3",
                            description = "...",
                            startTime = 1708734216,
                            endTime = 1708907016,
                            locations = listOf(
                                EventLocation(
                                    description = "SIEBEL",
                                    tags = listOf("TAG1", "TAG2"),
                                    latitude = 12.0,
                                    longitude = -80.0
                                )
                            ),
                            isAsync = false
                        ),
                        Shift(
                            eventId = "event 4",
                            name = "test 4",
                            description = "...",
                            startTime = 1708734216,
                            endTime = 1708907016,
                            locations = listOf(
                                EventLocation(
                                    description = "SIEBEL",
                                    tags = listOf("TAG1", "TAG2"),
                                    latitude = 12.0,
                                    longitude = -80.0
                                )
                            ),
                            isAsync = false
                        )
                        // Add more Shift objects as needed...
                    )
                )

                Log.d("Shifts Fetched", shifts.toString())
                shiftDao.clearTableAndInsertShifts(shifts.shifts)
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
