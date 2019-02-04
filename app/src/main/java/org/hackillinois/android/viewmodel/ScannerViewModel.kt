package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.model.CheckIn.CheckIn
import org.hackillinois.android.model.Event.TrackerContainer
import org.hackillinois.android.model.Event.UserEventPair
import org.hackillinois.android.repository.EventRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScannerViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance
    var eventsListLiveData: LiveData<List<Event>> = MutableLiveData<List<Event>>()
    var lastScanWasSuccessful = MutableLiveData<Boolean>()
    var lastUserIdScannedIn = MutableLiveData<String>()
    var shouldDisplayOverrideSwitch = MutableLiveData<Boolean>()

    val CHECK_IN_TEXT = "Check In"


    fun init() {
        eventsListLiveData = eventRepository.fetchAllEvents()
        // Hidden by default
        shouldDisplayOverrideSwitch.postValue(false)
    }

    fun checkInUser(checkIn: CheckIn) {
        App.getAPI().checkInUser(checkIn).enqueue(object : Callback<CheckIn> {
            override fun onFailure(call: Call<CheckIn>, t: Throwable) {
                // Request could not be made
                lastScanWasSuccessful.postValue(false)
                Log.e("ScanEvent", "Could not make request.")
            }

            override fun onResponse(call: Call<CheckIn>, response: Response<CheckIn>) {
                // Request went through
                if (response.isSuccessful) {
                    // Check-in was a success
                    lastScanWasSuccessful.postValue(true)
                    lastUserIdScannedIn.postValue(response.body()?.id)
                    Log.d("ScanEvent", "Request: ${call.request().body()}")
                    Log.d("ScanEvent", "Response: ${response.raw()}")
                } else {
                    // Log error
                    lastScanWasSuccessful.postValue(false)
                    Log.e("ScanEvent", "Request: ${call.request().body()}")
                    Log.e("ScanEvent", "Response: ${response.raw()}")
                }
            }
        })
    }

    fun markUserAsAttendingEvent(userEventPair: UserEventPair) {
        App.getAPI().markUserAsAttendingEvent(userEventPair).enqueue(object : Callback<TrackerContainer> {
            override fun onFailure(call: Call<TrackerContainer>, t: Throwable) {
                // Request could not be made
                lastScanWasSuccessful.postValue(false)
                Log.d("ScanEvent", "Could not make request.")
            }

            override fun onResponse(call: Call<TrackerContainer>, response: Response<TrackerContainer>) {
                // Request went through
                if (response.isSuccessful) {
                    // User marked as attending the event
                    lastUserIdScannedIn.postValue(response.body()?.userTracker?.userId)
                    lastScanWasSuccessful.postValue(true)
                    Log.e("ScanEvent", "Request: ${call.request().body()}")
                    Log.e("ScanEvent", "Response: ${response.raw()}")
                } else {
                    // Log error?
                    lastScanWasSuccessful.postValue(false)
                    Log.e("ScanEvent", "Request: ${call.request().body()}")
                    Log.e("ScanEvent", "Response: ${response.raw()}")
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
            Log.d("OverrideSwitch", "Visibility: ${shouldDisplayOverrideSwitch.value}")
        }

        override fun onNothingSelected(parentView: AdapterView<*>) {
            // your code here
        }
    }
}
