package org.hackillinois.android.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.extensions.isAfterNow
import org.hackillinois.android.extensions.isBeforeNow
import org.hackillinois.android.model.Event
import org.hackillinois.android.model.EventsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {
    val eventsListLiveData: MutableLiveData<List<Event>> = MutableLiveData()

    fun fetchEvents() {
        App.getAPI().allEvents.enqueue(object : Callback<EventsList> {
            override fun onFailure(call: Call<EventsList>, t: Throwable) { Log.w("HomeViewModel", "All events request failed!")}

            override fun onResponse(call: Call<EventsList>, response: Response<EventsList>) {
                val eventsList = response.body()
                val eventsHappeningNow = eventsList?.events?.asSequence()?.filter {
                    it.getStart().isBeforeNow()
                }?.filter {
                    it.getEnd().isAfterNow()
                }?.toMutableList()

                eventsListLiveData.postValue(eventsHappeningNow)
            }
        })
    }
}
