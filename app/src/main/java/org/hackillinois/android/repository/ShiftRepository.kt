package org.hackillinois.android.repository

import org.hackillinois.android.App

class ShiftRepository {
    private val shiftDao = App.database.shiftDao()

    // write fetchShifts() and refreshAll() functions
    // look at other repository functions for reference (i.e. LeaderboardRepository)

    companion object {
        val instance: ShiftRepository by lazy { ShiftRepository() }
    }
}
