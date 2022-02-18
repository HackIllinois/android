package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.EventCheckInResponse
import org.hackillinois.android.database.entity.EventCode

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
                Log.d("events returned", events.toString())

                // TODO FavoritesManager.updateFavoriteNotifications(, eventDao.getAllEventsList(), events)
                eventDao.clearTableAndInsertEvents(events)
            } catch (e: Exception) {
                Log.e("refreshAllEvents", e.toString())
            }
        }
    }

    companion object {
        private fun getEventCodeMessage(response: EventCheckInResponse): String {
            var responseString: String = ""
            when (response.status) {
                "Success" -> responseString = "Success! You received ${response.newPoints} points."
                "InvalidCode" -> responseString = "This code doesn't seem to be correct."
                "InvalidTime" -> responseString = "Make sure you have the right time."
                "AlreadyCheckedIn" -> responseString = "Looks like you're already checked in."
                else -> responseString = "Something isn't quite right."
            }
            return responseString
        }
        suspend fun checkInEvent(code: String): String {
            Log.d("send event token", code)
            var apiResponse: EventCheckInResponse = EventCheckInResponse(-1, -1, "")

            withContext(Dispatchers.IO) {
                try {
                    Log.d("Sending code: ", code)

                    apiResponse = App.getAPI().eventCodeCheckIn(EventCode(code))
                    Log.d("code sent!", apiResponse.toString())
                } catch (e: Exception) {
                        Log.e("Error - check in", e.toString())
                }
            }
            return getEventCodeMessage(apiResponse)
        }

        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
