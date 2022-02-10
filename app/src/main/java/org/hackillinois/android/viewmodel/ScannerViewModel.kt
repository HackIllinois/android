package org.hackillinois.android.viewmodel

import android.app.PendingIntent.getActivity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.EventCheckInResponse
import org.hackillinois.android.database.entity.EventCode
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.model.ScanStatus
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.rolesRepository
import org.json.JSONObject
import retrofit2.HttpException
import java.lang.Exception

class ScannerViewModel : ViewModel() {
    var lastScanStatus: MutableLiveData<ScanStatus> = MutableLiveData()
    lateinit var roles: LiveData<Roles>

    private val CHECK_IN_NAME = "Check-in"

    private lateinit var eventName: String

    fun init(eventName: String) {
        this.eventName = eventName
        this.roles = rolesRepository.fetch()
    }

    fun checkUserIntoEvent(eventId: String, userId: String, staffOverride: Boolean) {
        if (eventName == CHECK_IN_NAME) {
            val checkIn = CheckIn(userId, staffOverride, hasCheckedIn = true, hasPickedUpSwag = true)
            checkInUser(checkIn)
        } else {
            val userEventPair = UserEventPair(eventId, userId)
            markUserAsAttendingEvent(userEventPair)
        }
    }

    fun scanQrToCheckIn(eventId: String) : EventCheckInResponse {
        return checkIntoEvent(eventId)
    }

    fun checkIntoEvent(code : String) : EventCheckInResponse {
        var response = EventCheckInResponse(0, 0, "SCAN FAILED")
        viewModelScope.launch {
            try {
                response = EventRepository.checkInEvent(code)
                Log.d("CODE SUBMIT RESPONSE", response.toString())
            } catch (e: Exception) {
                Log.e("CODE SUBMIT RESPONSE", e.toString())
            }

        }
        return response
    }

    fun checkInUser(checkIn: CheckIn) {
        viewModelScope.launch {
            try {
                val ci: CheckIn = App.getAPI().checkInUser(checkIn)
                // Check-in was a success
                val userId = ci.id
                val scanStatus = ScanStatus(true, userId, "")
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        @Suppress("BlockingMethodInNonBlockingContext")
                        val error = JSONObject(e.response()?.errorBody()?.string())
                        val errorType = error.getString("type")
                        val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                            error.getString("message")
                        } else {
                            "Internal API error"
                        }
                        val scanStatus = ScanStatus(false, "", errorMessage)
                        lastScanStatus.postValue(scanStatus)
                    }
                    else -> {
                        val scanStatus = ScanStatus(false, "", "Request could not be made. Please try again.")
                        lastScanStatus.postValue(scanStatus)
                    }
                }
            }
        }
    }

    fun markUserAsAttendingEvent(userEventPair: UserEventPair) {
        viewModelScope.launch {
            try {
                val trackerContainer = App.getAPI().markUserAsAttendingEvent(userEventPair)
                val userId = trackerContainer.userTracker.userId
                val scanStatus = ScanStatus(true, userId, "")
                lastScanStatus.postValue(scanStatus)
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
                        val scanStatus = ScanStatus(false, "", errorMessage)
                        lastScanStatus.postValue(scanStatus)
                    }
                    else -> {
                        val scanStatus = ScanStatus(false, "",
                                "Request could not be made. Please try again.")
                        lastScanStatus.postValue(scanStatus)
                    }
                }
            }
        }
    }
}