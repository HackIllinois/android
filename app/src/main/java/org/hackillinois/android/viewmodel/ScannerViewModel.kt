package org.hackillinois.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.scanner.ScanStatus
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.rolesRepository
import kotlin.Exception

class ScannerViewModel : ViewModel() {
    var lastScanStatus: MutableLiveData<ScanStatus> = MutableLiveData()

    lateinit var roles: LiveData<Roles>

    lateinit var allEvents: LiveData<EventsList>

    private val CHECK_IN_NAME = "Check-in"

    private lateinit var eventName: String

    fun init(eventName: String) {
        this.eventName = eventName
        this.roles = rolesRepository.fetch()
        this.allEvents = liveData {
            emit(App.getAPI().allEvents())
        }
    }

    fun scanQrToCheckInEvent(eventId: String): EventCheckInResponse {
        return checkIntoEvent(eventId)
    }

    fun checkIntoEvent(code: String): EventCheckInResponse {
        var response = EventCheckInResponse(0, 0, "SCAN FAILED")
        viewModelScope.launch {
            try {
                response = EventRepository.checkInEvent(code)
                val scanStatus: ScanStatus
                if (response.status == "Success") {
                    scanStatus = ScanStatus(true, response.newPoints, response.status)
                    lastScanStatus.postValue(scanStatus)
                } else {
                    // no new points
                    scanStatus = ScanStatus(true, 0, response.status)
                    lastScanStatus.postValue(scanStatus)
                }
                Log.d("CODE SUBMIT RESPONSE", scanStatus.toString())
            } catch (e: Exception) {
                Log.e("CODE SUBMIT RESPONSE", e.toString())
            }
        }
        return response
    }

    fun scanQrToCheckInMeeting(eventId: String): MeetingCheckInResponse {
        return checkIntoMeeting(eventId)
    }

    fun checkIntoMeeting(eventId: String): MeetingCheckInResponse {
        var response = MeetingCheckInResponse("SCAN FAILED")
        viewModelScope.launch {
            try {
                response = EventRepository.checkInMeeting(eventId)
                if (response.status == "Success") {
                    val scanStatus = ScanStatus(true, 0, response.status)
                    lastScanStatus.postValue(scanStatus)
                } else {
                    val scanStatus = ScanStatus(false, 0, response.status)
                    lastScanStatus.postValue(scanStatus)
                }
            } catch (e: Exception) {
                Log.e("CODE SUBMIT RESPONSE", e.toString())
            }
        }
        return response
    }

    fun checkUserIntoEventAsStaff(qrCodeString: String, eventId: String): EventCheckInAsStaffResponse {
        val userId = qrCodeString // decodeJWT(qrCodeString)
        var response = EventCheckInAsStaffResponse(
            0,
            0,
            "SCAN FAILED",
            RSVPData(
                "",
                false,
                RegistrationData(
                    AttendeeData(listOf()),
                ),
            ),
        )
        viewModelScope.launch {
            try {
                response = EventRepository.checkInEventAsStaff(userId, eventId)
                Log.i("Check In", "Status: ${response.status}")
                Log.i("Check In", "Response: $response")
                lastScanStatus.postValue(
                    ScanStatus(
                        response.rsvpData != null,
                        0,
                        if (response.rsvpData == null) "Bad User Token" else response.status,
                        if (response.rsvpData != null) {
                            response
                                .rsvpData
                                .registrationData
                                .attendee
                                .dietary
                                .joinToString()
                        } else {
                            "Bad User Token"
                        },
                    ),
                )
            } catch (e: Exception) {
                Log.e("Staff Check In", e.toString())
            }
        }
        return response
    }

//    fun checkInUser(checkIn: CheckIn) {
//        viewModelScope.launch {
//            try {
//                val ci: CheckIn = App.getAPI().checkInUser(checkIn)
//                // Check-in was a success
//                val userId = ci.id
// //                val scanStatus = ScanStatus(true, userId, "")
// //                lastScanStatus.postValue(scanStatus)
//            } catch (e: Exception) {
//                when (e) {
//                    is HttpException -> {
//                        @Suppress("BlockingMethodInNonBlockingContext")
//                        val error = JSONObject(e.response()?.errorBody()?.string())
//                        val errorType = error.getString("type")
//                        val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
//                            error.getString("message")
//                        } else {
//                            "Internal API error"
//                        }
//                        val scanStatus = ScanStatus(false, "", errorMessage)
//                        lastScanStatus.postValue(scanStatus)
//                    }
//                    else -> {
//                        val scanStatus = ScanStatus(false, "", "Request could not be made. Please try again.")
//                        lastScanStatus.postValue(scanStatus)
//                    }
//                }
//            }
//        }
//    }

//    fun markUserAsAttendingEvent(userEventPair: UserEventPair) {
//        viewModelScope.launch {
//            try {
//                val trackerContainer = App.getAPI().markUserAsAttendingEvent(userEventPair)
//                val userId = trackerContainer.userTracker.userId
//                val scanStatus = ScanStatus(true, userId, "")
//                lastScanStatus.postValue(scanStatus)
//            } catch (e: Exception) {
//                when (e) {
//                    is HttpException -> {
//                        val error = JSONObject(e.response()?.errorBody()?.string())
//                        val errorType = error.getString("type")
//                        val errorMessage = if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
//                            error.getString("message")
//                        } else {
//                            "Internal API error"
//                        }
//                        val scanStatus = ScanStatus(false, "", errorMessage)
//                        lastScanStatus.postValue(scanStatus)
//                    }
//                    else -> {
//                        val scanStatus = ScanStatus(false, "",
//                                "Request could not be made. Please try again.")
//                        lastScanStatus.postValue(scanStatus)
//                    }
//                }
//            }
//        }
//    }
}
