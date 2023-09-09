package org.hackillinois.android

import okhttp3.ResponseBody
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.TimesWrapper
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.event.TrackerContainer
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.model.event.UserTokenEventIdPair
import org.hackillinois.android.model.leaderboard.LeaderboardList
import org.hackillinois.android.model.notification.NotificationTopics
import org.hackillinois.android.model.profile.ProfileList
import org.hackillinois.android.model.projects.ProjectsList
import org.hackillinois.android.notifications.DeviceToken
import retrofit2.Call
import retrofit2.http.*

interface API {

    // AUTH
    // note: @GET("auth/login/{provider}/") is not part of this file because the URL
    // is directly accessed in the LoginActivity.kt file to get to the OAuth page

    @GET("auth/roles/")
    suspend fun roles(): Roles

    // CHECK-IN

    @POST("checkin/")
    suspend fun checkInUser(@Body checkIn: CheckIn): CheckIn

    // EVENT

    @GET("event/")
    suspend fun allEvents(): EventsList

    @POST("event/")
    fun createEvent(@Body event: Event): Call<Event>

    @GET("event/{id}/")
    fun getEvent(@Path("id") id: String): Call<Event>

    @POST("event/track/")
    suspend fun markUserAsAttendingEvent(@Body userEventPair: UserEventPair): TrackerContainer

    @POST("event/checkin/")
    suspend fun eventCodeCheckIn(@Body token: EventCode): EventCheckInResponse

    @POST("event/staff/checkin/")
    suspend fun checkInUserAsStaff(@Body userTokenEventIdPair: UserTokenEventIdPair): EventCheckInAsStaffResponse

    // NOTIFICATIONS

    @POST("notifications/device/")
    suspend fun sendUserToken(@Body token: DeviceToken): DeviceToken

    @POST("notifications/update/")
    suspend fun updateNotificationTopics(): NotificationTopics

    // REGISTRATION

    @GET("registration/attendee/")
    suspend fun attendee(): Attendee

    // STAT

    @GET("stat/")
    fun stats(): Call<ResponseBody>

    // USER

    @GET("user/")
    suspend fun user(): User

    @GET("user/qr/")
    suspend fun qrCode(): QR

    // PROJECT

    @GET("project/")
    suspend fun allProjects(): ProjectsList

    // PROFILE

    @PUT("profile/")
    suspend fun updateProfile(@Body newProfile: Profile): Profile

    @GET("profile/")
    suspend fun currentProfile(): Profile

    @GET("profile/search/")
    suspend fun allProfiles(): ProfileList

    @GET("upload/blobstore/times/")
    suspend fun times(): TimesWrapper

    @GET("profile/leaderboard/?limit=10")
    suspend fun leaderboard(): LeaderboardList

    companion object {
        val BASE_URL = "https://adonix.hackillinois.org/"
    }
}
