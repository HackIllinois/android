package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.model.profile.ProfileList
import java.lang.Exception

class ProfileRepository {
    private val profileDao = App.database.profileDao()
    fun fetchProfile(): LiveData<Profile> {
        // make api call to get profile
        refresh()
        // get profile from Room database
        val profile = profileDao.getProfile()
        return profile
    }

    private fun refresh() {
        // runs in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // refreshes list of profiles and reinserts into the Dao
                val profile = App.getAPI().currentProfile()
                val profileList = ProfileList(listOf(profile))
                profileDao.clearTableAndInsertProfiles(profileList.profiles)
            } catch (e: Exception) {
                Log.e("PROFILE REFRESH", e.toString())
            }
        }
    }

    companion object {
        val instance: ProfileRepository by lazy { ProfileRepository() }
    }
}
