package org.hackillinois.android.viewmodel

// import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Profile
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.repository.ProfileRepository
import org.hackillinois.android.repository.attendeeRepository
import org.hackillinois.android.repository.qrRepository
import java.util.Timer
import java.util.TimerTask
class ProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository.instance

    lateinit var currentProfileLiveData: LiveData<Profile>
    lateinit var qr: LiveData<QR>
    lateinit var attendee: LiveData<Attendee>

    fun init() {
        // Log.d("ViewModelInit:", "Began init")
        currentProfileLiveData = profileRepository.fetchProfile()
        qr = qrRepository.fetch()
        attendee = attendeeRepository.fetch()
        // should refresh QR code every 15 seconds
        val timerObj = Timer()
        val timerTaskObj: TimerTask = object : TimerTask() {
            override fun run() {
                // Log.d("Fetched Qr Code:", "Ran")
                qr = qrRepository.fetch()
            }
        }
        // timerObj.schedule(timerTaskObj, 0, 15000)
        timerObj.scheduleAtFixedRate(timerTaskObj, 0, 15000)
    }

    fun fetchCurrentProfile() {
        currentProfileLiveData = profileRepository.fetchProfile()
    }
}
