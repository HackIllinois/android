package org.hackillinois.android.repository

import android.arch.lifecycle.LiveData
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import kotlin.concurrent.thread

class EventRepository {
    private val eventDao = App.getDatabase().eventDao()

    private val minutesTillStale: Long = 2
    private val secondsInMinute: Long = 60
    private val millisInSecond: Long = 1000
    private val millisTillStale = minutesTillStale * secondsInMinute * millisInSecond

    fun fetchAllEvents(): LiveData<List<Event>> {
        attemptToRefreshAll()
        return eventDao.getAllEvents()
    }

    fun fetchEventsHappeningAtTime(time: Long): LiveData<List<Event>> {
        attemptToRefreshAll()
        return eventDao.getAllEventsHappeningAtTime(time / 1000L)
    }

    fun fetchEvent(name: String): LiveData<Event> {
        attemptToRefreshSpecific(name)
        return eventDao.getEvent(name)
    }

    fun forceRefreshAll() {
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
        val response = App.getAPI().allEvents.execute()
        response?.let {
            val eventsList = it.body()
            val newRefreshed = System.currentTimeMillis()
            eventDao.clearTable()
            eventsList?.events?.forEach {
                it.lastRefreshed = newRefreshed
                eventDao.insert(it)
            }
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
        val response = App.getAPI().getEvent(name).execute()
        response?.let {
            val event = it.body()
            event?.let {
                event.lastRefreshed = System.currentTimeMillis()
                eventDao.insert(event)
            }
        }
    }

    companion object {
        val instance = EventRepository()
    }
}
