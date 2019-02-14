package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.model.event.EventsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class EventRepository {
    private val eventDao = App.getDatabase().eventDao()
    private val MILLIS_IN_SECOND = 1000L

    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / MILLIS_IN_SECOND)
    }

    fun fetchEventsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getEventsHappeningBetweenTimes(startTime / MILLIS_IN_SECOND, endTime / MILLIS_IN_SECOND)
    }

    fun fetchEvent(name: String): LiveData<Event> {
        refreshEvent(name)
        return eventDao.getEvent(name)
    }

    fun forceFetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / MILLIS_IN_SECOND)
    }

    fun fetchAllEvents(): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getAllEvents()
    }

    private fun refreshAll() {
        App.getAPI().allEvents.enqueue(object : Callback<EventsList> {
            override fun onResponse(call: Call<EventsList>, response: Response<EventsList>) {
                if (response.isSuccessful) {
                    val eventsList: List<Event> = response.body()?.events ?: return
                    thread { eventDao.clearTableAndInsertEvents(eventsList) }
                }
            }

            override fun onFailure(call: Call<EventsList>, t: Throwable) {}
        })
    }

    private fun refreshEvent(name: String) {
        App.getAPI().getEvent(name).enqueue(object : Callback<Event> {
            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                if (response.isSuccessful) {
                    val event: Event = response.body() ?: return
                    thread { eventDao.insert(event) }
                }
            }

            override fun onFailure(call: Call<Event>, t: Throwable) {}
        })
    }

    companion object {
        val instance: EventRepository by lazy { EventRepository() }
    }
}
