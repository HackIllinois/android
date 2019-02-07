package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import java.lang.Exception
import kotlin.concurrent.thread

class EventRepository {
    private val eventDao = App.getDatabase().eventDao()

    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        thread { refreshAll() }
        return eventDao.getAllEventsHappeningAtTime(time / 1000L)
    }

    fun fetchEventsHappeningBetweenTimes(startTime: Long, endTime: Long) : LiveData<List<Event>> {
        thread { refreshAll() }
        return eventDao.getEventsHappeningBetweenTimes(startTime / 1000L, endTime / 1000L)
    }

    fun fetchEvent(name: String): LiveData<Event> {
        thread { refreshEvent(name) }
        return eventDao.getEvent(name)
    }

    fun forceFetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        forceRefreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / 1000L)
    }

    private fun forceRefreshAll() {
        thread { refreshAll() }
    }

    private fun refreshAll() {
        try {
            val response = App.getAPI().allEvents.execute()

            response?.let {
                if (response.isSuccessful) {
                    val eventsList = it.body()
                    eventsList?.events?.let { events ->
                        eventDao.clearTableAndInsertEvents(events)
                    }
                }
            }
        } catch (exception: Exception) {
            Log.e("EventRepository", exception.message)
        }
    }

    private fun refreshEvent(name: String) {
        try {
            val response = App.getAPI().getEvent(name).execute()
            response?.let {
                if (response.isSuccessful) {
                    val event = it.body()
                    event?.let {
                        eventDao.insert(event)
                    }
                }
            }
        } catch (exception: Exception) {
            Log.d("EventRepository", exception.message)
        }
    }

    companion object {
        val instance = EventRepository()
    }
}
