package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Leaderboard
import java.lang.Exception

class LeaderboardRepository {
    private val leaderboardDao = App.database.leaderboardDao()

    fun fetchLeaderboard(): LiveData<List<Leaderboard>> {
        // 'refreshAll()' coroutine is called only when leaderboard button on bottom app bar clicked
        refreshAll()
        // locally stored database
        val lb = leaderboardDao.getLeaderboard()
        Log.d("LEADERBOARD CALL", lb.value.toString())
        return lb
    }

    fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val shop = App.getAPI().shop()
                Log.d("SHOP", shop.toString())
                // break
                val leaderboard = App.getAPI().leaderboard()
                Log.d("LEADERBOARD REFRESH ALL", leaderboard.toString())
                leaderboardDao.clearTableAndInsertProfiles(leaderboard.profiles)
            } catch (e: Exception) {
                Log.e("LEADERBOARD REFRESH ALL", e.toString())
            }
        }
    }

    companion object {
        val instance: LeaderboardRepository by lazy { LeaderboardRepository() }
    }
}
