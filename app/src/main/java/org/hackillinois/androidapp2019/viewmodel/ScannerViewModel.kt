package org.hackillinois.androidapp2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import org.hackillinois.androidapp2019.App
import org.hackillinois.androidapp2019.database.entity.Event
import org.hackillinois.androidapp2019.model.ScanStatus
import org.hackillinois.androidapp2019.model.checkin.CheckIn
import org.hackillinois.androidapp2019.model.event.TrackerContainer
import org.hackillinois.androidapp2019.model.event.UserEventPair
import org.hackillinois.androidapp2019.repository.EventRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance
    var eventsListLiveData: LiveData<List<Event>> = MutableLiveData<List<Event>>()
    var lastScanStatus: MutableLiveData<ScanStatus> = MutableLiveData()
    var shouldDisplayOverrideSwitch = MutableLiveData<Boolean>()

    private val CHECK_IN_TEXT = "Check In"

    fun init() {
        eventsListLiveData = eventRepository.fetchAllEvents()
        // Hidden by default
        shouldDisplayOverrideSwitch.postValue(false)
    }

    fun checkUserIntoEvent(eventName: String, userId: String, staffOverride: Boolean) {
        if (eventName == CHECK_IN_TEXT) {
            val hasCheckedIn = true
            val hasPickedUpSwag = true
            val checkIn = CheckIn(userId, staffOverride, hasCheckedIn, hasPickedUpSwag)
            checkInUser(checkIn)
        } else {
            val userEventPair = UserEventPair(eventName, userId)
            markUserAsAttendingEvent(userEventPair)
        }
    }

    fun checkInUser(checkIn: CheckIn) {
        App.getAPI().checkInUser(checkIn).enqueue(object : Callback<CheckIn> {
            override fun onFailure(call: Call<CheckIn>, t: Throwable) {
                var scanStatus = ScanStatus(false, "", "Request could not be made.")
                lastScanStatus.postValue(scanStatus)
            }

            override fun onResponse(call: Call<CheckIn>, response: Response<CheckIn>) {
                if (response.isSuccessful) {
                    // Check-in was a success
                    var userId = response.body()?.id.toString()
                    var scanStatus = ScanStatus(true, userId, "")
                    lastScanStatus.postValue(scanStatus)
                } else {
                    var scanStatus = ScanStatus(false, "", "Could not check in user.")
                    lastScanStatus.postValue(scanStatus)
                }
            }
        })
    }

    fun markUserAsAttendingEvent(userEventPair: UserEventPair) {
        App.getAPI().markUserAsAttendingEvent(userEventPair).enqueue(object : Callback<TrackerContainer> {
            override fun onFailure(call: Call<TrackerContainer>, t: Throwable) {
                var scanStatus = ScanStatus(false, "", "Request could not be made.")
                lastScanStatus.postValue(scanStatus)
            }

            override fun onResponse(call: Call<TrackerContainer>, response: Response<TrackerContainer>) {
                if (response.isSuccessful) {
                    // User marked as attending the event
                    var userId = response.body()?.userTracker?.userId.toString()
                    var scanStatus = ScanStatus(true, userId, "")
                    lastScanStatus.postValue(scanStatus)
                } else {
                    var scanStatus = ScanStatus(false, "", "Could not check in user.")
                    lastScanStatus.postValue(scanStatus)
                }
            }
        })
    }

    /**
     * Called when an event is selected from the events list.
     */
    var onEventSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long) {
            // Spinner should be displayed if the event is Check In, but not otherwise
            val spinner = parentView as Spinner
            shouldDisplayOverrideSwitch.postValue(spinner.selectedItem == CHECK_IN_TEXT)
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // your code here
        }
    }
}
