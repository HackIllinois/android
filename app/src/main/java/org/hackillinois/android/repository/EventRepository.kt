package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import android.util.Log
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import java.lang.Exception
import kotlin.concurrent.thread

class EventRepository {
    private val eventDao = App.getDatabase().eventDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        attemptToRefreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / 1000L)
    }

    fun fetchEventsHappeningBetweenTimes(startTime: Long, endTime: Long) : LiveData<List<Event>> {
        attemptToRefreshAll()
        return eventDao.getEventsHappeningBetweenTimes(startTime / 1000L, endTime / 1000L)
    }

    fun fetchEvent(name: String): LiveData<Event> {
        attemptToRefreshSpecific(name)
        return eventDao.getEvent(name)
    }

    fun forceFetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        forceRefreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / 1000L)
    }

    private fun forceRefreshAll() {
        thread {
            refreshAll()
        }
    }

    /**
     * This method determines, on a separate thread, if we need to update the entire events table
     * We decide to update when the least recently updated event has reached a "stale" state
     * In this case, we clear the table entirely (in case events were removed on the web side) and
     * replace them with a fresh set from the API
     */
    private fun attemptToRefreshAll() {
        thread {
            val timeRefreshed = eventDao.getTimeOfOldestRefreshedEvent() ?: 0L

            // is it time to refresh?
            if (timeRefreshed < System.currentTimeMillis() - millisTillStale) {
                refreshAll()
            }
        }
    }

    private fun refreshAll() {
        try {
            val response = App.getAPI().allEvents.execute()

            response?.let {
                if (response.isSuccessful) {
                    val eventsList = it.body()
                    val newRefreshed = System.currentTimeMillis()
                    eventsList?.events?.forEach { event ->
                        event.lastRefreshed = newRefreshed
                    }
                    eventsList?.events?.let { events ->
                        eventDao.clearTableAndInsertEvents(events)
                    }
                }
            }
        } catch (exception: Exception) {
            Log.e("EventRepository", exception.message)
        }
    }

    private fun attemptToRefreshSpecific(name: String) {
        thread {
            val eventExists = eventDao.hasUpdatedEvent(name, System.currentTimeMillis() - millisTillStale) != null
            if (!eventExists) {
                refreshEvent(name)
            }
        }
    }

    private fun refreshEvent(name: String) {
        try {
            val response = App.getAPI().getEvent(name).execute()
            response?.let {
                if (response.isSuccessful) {
                    val event = it.body()
                    event?.let {
                        event.lastRefreshed = System.currentTimeMillis()
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
