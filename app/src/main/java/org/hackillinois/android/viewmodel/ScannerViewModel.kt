package org.hackillinois.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.scanner.ScanStatus
import org.hackillinois.android.model.shop.ItemInstance
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.rolesRepository
import org.json.JSONObject
import retrofit2.HttpException
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

    fun attendeeCheckInEvent(eventId: String) {
        viewModelScope.launch {
            try {
                val response = EventRepository.checkInEvent(eventId)
                val scanStatus: ScanStatus
                // Check if attendee successfully checked into the event
                if (response.status == "Success") {
//                    scanStatus = ScanStatus(response.newPoints, response.status)
//                    lastScanStatus.postValue(scanStatus)
                } else {
//                    scanStatus = ScanStatus(0, response.status)
//                    lastScanStatus.postValue(scanStatus)
                }
            } catch (e: Exception) {
                Log.e("ATTENDEE - CHECK IN EVENT", e.toString())
            }
        }
    }

    fun staffCheckInMeeting(eventId: String) {
        viewModelScope.launch {
            try {
                val response = EventRepository.checkInMeeting(eventId)
//                val scanStatus = ScanStatus(0, response.status)
//                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                Log.e("STAFF - MEETING CHECK IN", e.toString())
            }
        }
    }

    fun staffCheckInAttendee(userId: String, eventId: String) {
        viewModelScope.launch {
            try {
                val response = EventRepository.checkInAttendee(userId, eventId)
//                val scanStatus = ScanStatus(0, response.status, response.rsvpData.registrationData.attendee.dietary.joinToString())
//                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                Log.e("STAFF - ATTENDEE CHECK IN", e.toString())
            }
        }
    }

    fun purchaseItem(body: ItemInstance) {
        viewModelScope.launch {
            try {
                App.getAPI().buyShopItem(body)
                val message = "You have successfully redeemed your points at the Point Shop!"
                val scanStatus = ScanStatus(0, message, null, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                if (e is HttpException) {
                    val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                    error = jsonObject.optString("error", e.message.toString())
                }
                Log.e("PURCHASE ITEM ERROR", error)
                val scanStatus = ScanStatus(0, "reason: $error", null, false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }
}
