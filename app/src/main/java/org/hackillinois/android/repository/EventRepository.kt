package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*

class EventRepository {
    private val eventDao = App.database.eventDao()

    fun fetchEvents(): LiveData<List<Event>> {
        // make api call to fetch events
        refreshAllEvents()
        // get events from Room database
        val events = eventDao.getAllEvents()
        return events
    }
    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        return eventDao.getAllEventsHappeningAtTime(time / MILLIS_IN_SECOND)
    }

    fun fetchEventsHappeningBetweenTimes(startTime: Long, endTime: Long): LiveData<List<Event>> {
        return eventDao.getEventsHappeningBetweenTimes(startTime / MILLIS_IN_SECOND, endTime / MILLIS_IN_SECOND)
    }

    fun fetchEvent(id: String): LiveData<Event> {
        return eventDao.getEvent(id)
    }

    fun fetchEventsAfter(currentTime: Long): LiveData<List<Event>> {
        return eventDao.getEventsAfter(currentTime / 1000L)
    }

    fun refreshAllEvents() {
        // ensures database operation happens on the IO dispatcher
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val events = App.getAPI().allEvents().events
                Log.d("Events Fetched", events.toString())
                eventDao.clearTableAndInsertEvents(events)
            } catch (e: Exception) {
                Log.e("REFRESH EVENTS", e.toString())
            }
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
