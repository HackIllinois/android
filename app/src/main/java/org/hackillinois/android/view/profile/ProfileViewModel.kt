package org.hackillinois.android.view.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>
    lateinit var currentProfileLiveData: LiveData<Profile>

    fun init() {
        allProfilesLiveData = profileRepository.fetchAllProfiles()
        currentProfileLiveData = profileRepository.fetchCurrentProfile()

//        Log.d("TAG", "ALL PROFILES" + allProfilesLiveData.value!!.get(0).firstName)
    }
}