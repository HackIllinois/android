package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Leaderboard
import org.hackillinois.android.repository.LeaderboardRepository

class LeaderboardViewModel : ViewModel() {
    val leaderboardRepository = LeaderboardRepository.instance

    lateinit var leaderboardLiveData: LiveData<List<Leaderboard>>

    fun init() {
        leaderboardLiveData = leaderboardRepository.fetchLeaderboard()
    }
}
