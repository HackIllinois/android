package org.hackillinois.android.repository

import androidx.lifecycle.LiveData
import org.hackillinois.android.App
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.model.event.EventsList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class EventRepository {
    private val eventDao = App.database.eventDao()

    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / MILLIS_IN_SECOND)
    }

    fun fetchEventsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getEventsHappeningBetweenTimes(startTime / MILLIS_IN_SECOND, endTime / MILLIS_IN_SECOND)
    }

    fun fetchEvent(id: String): LiveData<Event> {
        refreshAll()
        return eventDao.getEvent(id)
    }

    fun fetchEventsAfter(currentTime: Long): LiveData<List<Event>> {
        refreshAll()
        return eventDao.getEventsAfter(currentTime / 1000L)
    }

    private fun refreshAll() {
        App.getAPI().allEvents().enqueue(object : Callback<EventsList> {
            override fun onResponse(call: Call<EventsList>, response: Response<EventsList>) {
                if (response.isSuccessful) {
                    val eventsList: List<Event> = response.body()?.events ?: return
                    thread { eventDao.clearTableAndInsertEvents(eventsList) }
                }
            }

            override fun onFailure(call: Call<EventsList>, t: Throwable) {}
        })
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
