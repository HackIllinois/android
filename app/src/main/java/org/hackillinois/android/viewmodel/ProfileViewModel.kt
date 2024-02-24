package org.hackillinois.android.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.profile.Ranking
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.qrRepository
import org.hackillinois.android.repository.rolesRepository
import java.util.Timer
import java.util.TimerTask
class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var currentProfileLiveData: LiveData<Profile>
    lateinit var qr: LiveData<QR>
    var ranking: MutableLiveData<Ranking> = MutableLiveData()
    lateinit var roles: LiveData<Roles>
    lateinit var timerObj: Timer
    private var isTimerRunning = false

    fun init() {
        this.roles = rolesRepository.fetch()
        fetchRanking()
        // Creates livedata for view to observe
        currentProfileLiveData = profileRepository.fetchProfile()
        // Initial qr code fetching
        qr = qrRepository.fetch()
        startTimer()
    }

    fun startTimer() {
        // should refresh QR code every 15 seconds using Timer() class
        if (!isTimerRunning) {
            timerObj = Timer()
            val timerTaskObj: TimerTask = object : TimerTask() {
                override fun run() {
                    Log.d("QR FETCH", "...")
                    qr = qrRepository.fetch()
                }
            }
            // Runs TimerTask every 15 seconds, with a 0 second delay upon the call of init().
            timerObj.scheduleAtFixedRate(timerTaskObj, 0, 15000)
            isTimerRunning = true
        }
    }

    fun stopTimer() {
        if (isTimerRunning && ::timerObj.isInitialized) {
            timerObj.cancel()
            isTimerRunning = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("OnCleared", "QR")
        stopTimer()
    }

    private fun fetchRanking() {
        viewModelScope.launch {
            try {
                val response = App.getAPI().profileRanking()
                ranking.postValue(response)
            } catch (e: Exception) {
                Log.e("Couldn't fetch ranking", e.toString())
            }
        }
    }
}
