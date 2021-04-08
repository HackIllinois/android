package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.Event
import org.json.JSONObject
import retrofit2.HttpException
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
                // TODO FavoritesManager.updateFavoriteNotifications(, eventDao.getAllEventsList(), events)
                eventDao.clearTableAndInsertEvents(events)
            } catch (e: Exception) {
                Log.e("refreshAllEvents", e.toString())
            }
        }
    }

    companion object {
        suspend fun checkInEvent(code: String): String {
            Log.d("send event token", code)

//            var gson = Gson()
//            var jsonString = gson.toJson((EventCode(code)))
            withContext(Dispatchers.IO) {
                try {
                    Log.d("Sending code: ", code)

                    val response = App.getAPI().eventCodeCheckIn(EventCode(code))
                    Log.d("code sent!", response.toString())
                    return@withContext response
                } catch (e: Exception) {
                    when (e) {
                        is HttpException -> {
                            val error = JSONObject(e.response()?.errorBody()?.string())
                            val errorType = error.getString("type")
                            if (errorType == "ATTRIBUTE_MISMATCH_ERROR") {
                                error.getString("message")
                            } else {
                                "Internal API error"
                            }
                        }

                        else -> Log.e("Error - check in", e.toString())
                    }
                }
            }
            return "Error"
        }

        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
