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

    fun init() {
        this.roles = rolesRepository.fetch()
        this.allEvents = liveData {
            emit(App.getAPI().allEvents())
        }
    }

    fun staffCheckInMeeting(eventId: String): MeetingCheckInResponse {
        var response = MeetingCheckInResponse("SCAN FAILED")
        viewModelScope.launch {
            try {
                response = EventRepository.checkInMeeting(eventId)
                if (response.status == "Success") {
                    val scanStatus = ScanStatus(0, response.status)
                    lastScanStatus.postValue(scanStatus)
                } else {
                    val scanStatus = ScanStatus(0, response.status)
                    lastScanStatus.postValue(scanStatus)
                }
            } catch (e: Exception) {
                Log.e("CODE SUBMIT RESPONSE", e.toString())
            }
        }
        return response
    }

    fun staffCheckInAttendee(userId: String, eventId: String): StaffCheckInResponse {
        var response = StaffCheckInResponse(
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
                response = EventRepository.checkInAttendee(userId, eventId)
                val scanStatus = ScanStatus(0,
                    response.status,
                    response.rsvpData.registrationData.attendee.dietary.joinToString())
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                Log.e("Staff Check In", e.toString())
            }
        }
        return response
    }

    fun attendeeCheckInEvent(code: String): AttendeeCheckInResponse {
        var response = AttendeeCheckInResponse(0, 0, "SCAN FAILED")
        viewModelScope.launch {
            try {
                response = EventRepository.checkInEvent(code)
                val scanStatus: ScanStatus
                if (response.status == "Success") {
                    scanStatus = ScanStatus(response.newPoints, response.status)
                    lastScanStatus.postValue(scanStatus)
                } else {
                    // no new points
                    scanStatus = ScanStatus(0, response.status)
                    lastScanStatus.postValue(scanStatus)
                }
            } catch (e: Exception) {
                Log.e("CODE SUBMIT RESPONSE", e.toString())
            }
        }
        return response
    }
}
