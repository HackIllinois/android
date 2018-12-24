package org.hackillinois.android.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.model.Event
import org.hackillinois.android.model.EventsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScheduleViewModel : ViewModel() {
    val eventsListLiveData: MutableLiveData<List<Event>> = MutableLiveData()

    fun fetchEvents() {
        App.getAPI().allEvents.enqueue(object : Callback<EventsList> {
            override fun onFailure(call: Call<EventsList>, t: Throwable) { Log.w("ScheduleViewModel", "All events request failed!")}

            override fun onResponse(call: Call<EventsList>, response: Response<EventsList>) {
                val eventsList = response.body()
                eventsListLiveData.postValue(eventsList?.events)
            }
        })
    }
}
