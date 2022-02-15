package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.model.leaderboard.LeaderboardEntity
import org.hackillinois.android.repository.LeaderboardRepository

class LeaderboardViewModel : ViewModel() {
    private val leaderboardRepository = LeaderboardRepository.instance

    lateinit var leaderboardLiveData: LiveData<List<LeaderboardEntity>>

    fun init() {
        leaderboardLiveData = leaderboardRepository.fetchLeaderboard()
    }
}
