package org.hackillinois.android.model.event

import com.google.android.gms.maps.model.LatLng

data class IndoorMapAndDirectionInfo(
    val locationDescription: String,
    val indoorMapResource: Int,
    val latLng: LatLng,
)

data class EventCode(val code: String)

data class MeetingEventId(val eventId: String)

data class UserEventPair(val userToken: String, val eventId: String)

data class MeetingCheckInResponse(var status: String)

data class AttendeeCheckInResponse(
    var newPoints: Int,
    var totalPoints: Int,
    var status: String,
)

data class StaffCheckInResponse(
    var newPoints: Int,
    var totalPoints: Int,
    var status: String,
    var rsvpData: RSVPData,
)

data class RSVPData(
    val id: String,
    val isAttending: Boolean,
    val registrationData: RegistrationData,
)

data class RegistrationData(
    val attendee: AttendeeData,
)

data class AttendeeData(
    val dietary: List<String>,
)
