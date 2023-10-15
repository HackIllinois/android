package org.hackillinois.android.repository

import android.util.Log
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.*

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
        // TODO: Use this function
        private fun getEventCodeMessage(response: AttendeeCheckInResponse): String {
            var responseString: String = ""
            Log.d("RESPONSE STATUS", response.status.toString())
            when (response.status) {
                "Success" -> responseString = "Success! You received ${response.newPoints} points."
                "InvalidCode" -> responseString = "This code doesn't seem to be correct."
                "InvalidTime" -> responseString = "Make sure you have the right time."
                "AlreadyCheckedIn" -> responseString = "Looks like you're already checked in."
                else -> responseString = "Something isn't quite right."
            }
            return responseString
        }

        suspend fun checkInEvent(eventId: String): AttendeeCheckInResponse {
            var apiResponse = AttendeeCheckInResponse(-1, -1, "")

            withContext(Dispatchers.IO) {
                try {
                    apiResponse = App.getAPI().eventCheckIn(EventCode(eventId))
                } catch (e: Exception) {
                    Log.e("Error - check in", e.toString())
                }
            }
            return apiResponse
        }

        suspend fun checkInMeeting(eventId: String): MeetingCheckInResponse {
            var apiResponse = MeetingCheckInResponse("")

            withContext(Dispatchers.IO) {
                try {
                    val body = MeetingEventId(eventId)
                    apiResponse = App.getAPI().staffMeetingCheckIn(body) // 200: status = "Success"
                } catch (e: Exception) {
                    apiResponse.status = e.message.toString()
                    Log.d("STAFF MEETING API ERROR", "${e.message}")
                }
            }
            return apiResponse
        }

        suspend fun checkInAttendee(userToken: String, eventId: String): StaffCheckInResponse {
            val userTokenEventIdPair = UserEventPair(userToken, eventId)
            var apiResponse = StaffCheckInResponse(-1, -1, "", RSVPData("", false, RegistrationData(AttendeeData(listOf()))),)

            withContext(Dispatchers.IO) {
                try {
                    Log.d("Sending code: ", userTokenEventIdPair.toString())
                    apiResponse = App.getAPI().staffAttendeeCheckIn(userTokenEventIdPair)
                } catch (e: Exception) {
                    Log.e("Error - check in", e.toString())
                }
            }
            return apiResponse
        }

        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
