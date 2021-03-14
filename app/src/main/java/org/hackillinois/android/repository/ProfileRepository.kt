package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Profile
import java.lang.Exception

class ProfileRepository {
    private val userDao = App.database.userDao()
    private val profileDao = App.database.profileDao()

    fun fetchCurrentProfile(): LiveData<Profile> {
        var currentProfileId = userDao.getUser().value?.id
        if (currentProfileId == null) {
            currentProfileId = ""
        }
        return fetchProfile(currentProfileId)
    }

    fun fetchProfile(profileId: String): LiveData<Profile> {
        refreshAll()
        return profileDao.getProfile(profileId)
    }

    fun fetchAllProfiles(): LiveData<List<Profile>> {
        refreshAll()
        return profileDao.getAllProfiles()
    }

    fun fetchProfilesWithInterests(interests: List<String>): LiveData<List<Profile>> {
        refreshAll()
        return profileDao.getProfilesWithInterests(interests)
    }

    private fun refreshAll() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d("TAG", App.getAPI().currentProfile().firstName)
                val profileList = App.getAPI().allProfiles()
                Log.d("TAG", profileList.toString())
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
