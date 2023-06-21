package org.hackillinois.android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.qrRepository
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*

class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var allProfilesLiveData: LiveData<List<Profile>>
    lateinit var currentProfileLiveData: LiveData<Profile>
    lateinit var qr: LiveData<QR>
    lateinit var attendee: LiveData<Attendee>

    fun init() {
        allProfilesLiveData = profileRepository.fetchAllProfiles()
        currentProfileLiveData = profileRepository.fetchCurrentProfile()
        qr = qrRepository.fetch()
        attendee = attendeeRepository.fetch()
        val timerObj = Timer()
        val timerTaskObj: TimerTask = object : TimerTask() {
            override fun run() {
                qr = qrRepository.fetch()
            }
        }
        timerObj.schedule(timerTaskObj, 0, 15000)
    }

    fun updateProfile(newProfile: Profile) {
        Log.d("TAG", "Profile update")
        viewModelScope.launch {
            try {
                val response = App.getAPI().updateProfile(newProfile)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        val error = JSONObject(e.response()?.errorBody()?.string())
                        val errorType = error.getString("type")
                        if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
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
