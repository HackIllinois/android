package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.model.leaderboard.LeaderboardEntity
import java.lang.Exception

class LeaderboardRepository {
    private val leaderboardDao = App.database.leaderboardDao()

    fun fetchLeaderboard(): LiveData<List<LeaderboardEntity>> {
        Log.d("profile call", "fetchAllProfiles")
        refreshAll()
        return leaderboardDao.getLeaderboard()
    }

    fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val leaderboard = App.getAPI().leaderboard()
                leaderboardDao.clearTableAndInsertProfiles(leaderboard.profiles)
            } catch (e: Exception) {
                Log.e("Profilerepo refreshAll", e.toString())
            }
        }
    }

    companion object {
        val instance: LeaderboardRepository by lazy { LeaderboardRepository() }
    }
}
