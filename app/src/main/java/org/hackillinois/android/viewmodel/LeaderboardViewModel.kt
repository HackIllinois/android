package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository

class LeaderboardViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>

    fun init() {
        allProfilesLiveData = profileRepository.fetchLeaderboard()
    }
}
