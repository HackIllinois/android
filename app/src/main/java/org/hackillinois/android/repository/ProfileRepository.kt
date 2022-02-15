package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Profile
import java.lang.Exception

class ProfileRepository {
    private val profileDao = App.database.profileDao()

    fun fetchCurrentProfile() = liveData {
        Log.d("profile call", "fetchcurrentprofile")
        emit(App.getAPI().currentProfile())
    }

    fun fetchProfile(profileId: String): LiveData<Profile> {
        Log.d("profile call", "fetchprofile")

        refreshAll()
        return profileDao.getProfile(profileId)
    }

    fun fetchAllProfiles(): LiveData<List<Profile>> {
        Log.d("profile call", "fetchAllProfiles")
        refreshAll()
        return profileDao.getAllProfiles()
    }

//    fun fetchProfilesWithInterests(interests: List<String>): LiveData<List<Profile>> {
//        Log.d("profile call", "fetchprofileswithinterests")
//
//        refreshAll()
//        return profileDao.getProfilesWithInterests(interests)
//    }

    fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val profileList = App.getAPI().allProfiles()
                profileDao.clearTableAndInsertProfiles(profileList.profiles)
            } catch (e: Exception) {
                Log.e("Profilerepo refreshAll", e.toString())
            }
        }
    }

    companion object {
        val instance: ProfileRepository by lazy { ProfileRepository() }
    }
}