package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.event.AttendeeCheckInResponse
import org.hackillinois.android.model.event.AttendeeData
import org.hackillinois.android.model.event.EventCode
import org.hackillinois.android.model.event.MeetingCheckInResponse
import org.hackillinois.android.model.event.MeetingEventId
import org.hackillinois.android.model.event.RSVPData
import org.hackillinois.android.model.event.RegistrationData
import org.hackillinois.android.model.event.StaffCheckInResponse
import org.hackillinois.android.model.event.UserEventPair
import org.json.JSONObject
import retrofit2.HttpException

class EventRepository {
    private val eventDao = App.database.eventDao()

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

    suspend fun refreshAllEvents() {
        // ensures database operation happens on the IO dispatcher
        withContext(Dispatchers.IO) {
            try {
                val events = App.getAPI().allEvents().events
                Log.d("Events Fetched", events.toString())
                // TODO FavoritesManager.updateFavoriteNotifications(, eventDao.getAllEventsList(), events)
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
