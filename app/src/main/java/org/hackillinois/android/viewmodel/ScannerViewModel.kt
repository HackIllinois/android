package org.hackillinois.android.viewmodel

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.scanner.EventId
import org.hackillinois.android.model.scanner.MentorId
import org.hackillinois.android.model.scanner.ScanStatus
import org.hackillinois.android.model.scanner.UserEventPair
import org.hackillinois.android.model.shop.ItemInstance
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

    fun submitMeetingAttendance(body: EventId) {
        viewModelScope.launch {
            try {
                App.getAPI().staffMeetingCheckIn(body)
                val message = "Your meeting attendance has been recorded."
                val scanStatus = ScanStatus(message, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                try {
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", e.message.toString())
                    }
                } catch (e: Exception) { }
                Log.e("STAFF MEETING ERROR", error)
                val scanStatus = ScanStatus("Scan failed: $error", false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }

    fun checkInAttendee(body: UserEventPair) {
        viewModelScope.launch {
            try {
                Log.d("USEREVENTPAIR", body.toString())
                val response = App.getAPI().scanAttendee(body)
                val dietaryRestrictions = response.dietaryRestrictions.joinToString()
                val message = "Attendee has the following dietary restrictions: $dietaryRestrictions."
                val scanStatus = ScanStatus(message, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                try {
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", e.message.toString())
                    }
                } catch (e: Exception) { }
                Log.e("CHECK IN ATTENDEE ERROR", error)
                val scanStatus = ScanStatus("Scan failed: $error", false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }

    fun checkInEvent(body: EventId) {
        viewModelScope.launch {
            try {
                val response = App.getAPI().scanEvent(body)
                val message = "${response.points} points have been added to your total score."
                val scanStatus = ScanStatus(message, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                try {
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", e.message.toString())
                    }
                } catch (e: Exception) { }
                Log.e("CHECK IN EVENT ERROR", error)
                val scanStatus = ScanStatus("Scan failed: $error", false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }

    fun checkInMentor(body: MentorId) {
        viewModelScope.launch {
            try {
                val response = App.getAPI().scanMentor(body)
                val message = "${response.points} points have been added to your total score."
                val scanStatus = ScanStatus(message, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                try {
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", e.message.toString())
                    }
                } catch (e: Exception) { }
                Log.e("CHECK IN MENTOR ERROR", error)
                val scanStatus = ScanStatus("Scan failed: $error", false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }

    fun purchaseItem(body: ItemInstance) {
        viewModelScope.launch {
            try {
                Log.d("ITEMINSTANCE", body.toString())
                val itemName = App.getAPI().buyShopItem(body)
                val message = "You have successfully redeemed ${itemName.itemName} from the Point Shop!"
                val scanStatus = ScanStatus(message, true)
                lastScanStatus.postValue(scanStatus)
            } catch (e: Exception) {
                var error = e.message.toString()
                try {
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", e.message.toString())
                    }
                } catch (e: Exception) { }
                Log.e("PURCHASE ITEM ERROR", error)
                val scanStatus = ScanStatus("Scan failed: $error", false)
                lastScanStatus.postValue(scanStatus)
            }
        }
    }
}
