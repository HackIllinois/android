package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.ShopItem
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.ShopRepository
import java.util.Timer
import java.util.TimerTask

class ShopViewModel : ViewModel() {
    private val shopRepository = ShopRepository.instance
    private val profileRepository = ProfileRepository.instance

    lateinit var shopLiveData: LiveData<List<ShopItem>>
    lateinit var profileLiveData: LiveData<Profile>

    lateinit var timerObj: Timer
    private var isTimerRunning = false
    private var isAttendee = true

    fun init(inIsAttendee: Boolean) {
        // initial fetch
        shopLiveData = shopRepository.fetchShop()
        isAttendee = inIsAttendee
        if (isAttendee) {
            profileLiveData = profileRepository.fetchProfile()
        }

        startTimer()
    }

    fun startTimer() {
        // runs TimerTask every 10 seconds to fetch profile and shop data
        if (!isTimerRunning) {
            timerObj = Timer()
            val timerTaskObj: TimerTask = object : TimerTask() {
                override fun run() {
                    shopLiveData = shopRepository.fetchShop()
                    if (isAttendee) {
                        profileLiveData = profileRepository.fetchProfile()
                    }
                }
            }
            timerObj.scheduleAtFixedRate(timerTaskObj, 0, 10000)
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
        stopTimer()
    }
}
