package org.hackillinois.android.view.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.repository.ProfileRepository
import org.json.JSONObject
import retrofit2.HttpException

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>
    lateinit var currentProfileLiveData: LiveData<Profile>

    fun init() {

        allProfilesLiveData = profileRepository.fetchAllProfiles()
        currentProfileLiveData = profileRepository.fetchCurrentProfile()

//        Log.d("TAG", "ALL PROFILES" + allProfilesLiveData.value!!.get(0).firstName)
    }

    fun updateProfile(newProfile: Profile) {
        Log.d("TAG", "Profile update")
        viewModelScope.launch {
            try {
                val response = App.getAPI().updateProfile(newProfile)
                Log.d("TAG", "RESPONSE " + response.toString())
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val error = JSONObject(e.response()?.errorBody()?.string())
                        val errorType = error.getString("type")
                        val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                            error.getString("message")
                        } else {
                            "Internal API error"
                        }
                    }
                }
            }
        }
        profileRepository.refreshAll()
    }
}