package org.hackillinois.android.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>

    fun init() {
        allProfilesLiveData = profileRepository.fetchAllProfiles()
    }
}