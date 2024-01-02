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
//        TODO: Use this function
//        private fun getEventCodeMessage(response: AttendeeCheckInResponse): String {
//            var responseString: String = ""
//            Log.d("RESPONSE STATUS", response.status.toString())
//            when (response.status) {
//                "Success" -> responseString = "Success! You received ${response.newPoints} points."
//                "InvalidCode" -> responseString = "This code doesn't seem to be correct."
//                "InvalidTime" -> responseString = "Make sure you have the right time."
//                "AlreadyCheckedIn" -> responseString = "Looks like you're already checked in."
//                else -> responseString = "Something isn't quite right."
//            }
//            return responseString
//        }

        suspend fun checkInEvent(eventId: String): AttendeeCheckInResponse {
            var apiResponse = AttendeeCheckInResponse(-1, -1, "")

            withContext(Dispatchers.IO) {
                try {
                    apiResponse = App.getAPI().eventCheckIn(EventCode(eventId))
                } catch (e: Exception) {
                    Log.e("ATTENDEE EVENT CHECK IN", e.toString())
                }
            }
            return apiResponse
        }

        suspend fun checkInMeeting(eventId: String): MeetingCheckInResponse {
            var apiResponse = MeetingCheckInResponse("")

            withContext(Dispatchers.IO) {
                try {
                    val body = MeetingEventId(eventId)
                    App.getAPI().staffMeetingCheckIn(body)
                    apiResponse.status = "Success! Your meeting attendance has been recorded!"
                } catch (e: Exception) {
                    var error = "Unknown error"
                    if (e is HttpException) {
                        val jsonObject = JSONObject("" + e.response()?.errorBody()?.string())
                        error = jsonObject.optString("error", "Unknown error")
                    }
                    apiResponse.status = "Scan failed: $error"
                    Log.d("STAFF MEETING CHECK IN ERROR", apiResponse.status)
                }
            }
            return apiResponse
        }

        suspend fun checkInAttendee(userToken: String, eventId: String): StaffCheckInResponse {
            val userTokenEventIdPair = UserEventPair(userToken, eventId)
            var apiResponse = StaffCheckInResponse(-1, -1, "", RSVPData("", false, RegistrationData(AttendeeData(listOf()))))

            withContext(Dispatchers.IO) {
                try {
                    apiResponse = App.getAPI().staffEventCheckIn(userTokenEventIdPair)
                } catch (e: Exception) {
                    Log.e("STAFF EVENT CHECK IN", e.toString())
                    apiResponse.status = "Something isn't quite right."
                }
            }
            return apiResponse
        }

        const val MILLIS_IN_SECOND = 1000L
        val instance: EventRepository by lazy { EventRepository() }
    }
}
