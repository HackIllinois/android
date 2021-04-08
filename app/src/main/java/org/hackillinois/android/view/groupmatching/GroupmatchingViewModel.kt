package org.hackillinois.android.view.groupmatching

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository

class GroupmatchingViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>

    fun init() {
        allProfilesLiveData =
        Transformations.map(profileRepository.fetchAllProfiles()) {
            it.shuffled()
        }
    }
}