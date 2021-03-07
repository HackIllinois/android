package org.hackillinois.android

import org.hackillinois.android.model.auth.Code
import org.hackillinois.android.model.auth.JWT
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.event.TrackerContainer
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.model.notification.NotificationTopics
import org.hackillinois.android.notifications.DeviceToken

import okhttp3.ResponseBody
import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.TimesWrapper
import org.hackillinois.android.model.profile.ProfileList
import org.hackillinois.android.model.projects.ProjectsList
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    // AUTH

    @POST("auth/code/{provider}/")
    suspend fun getJWT(
        @Path("provider") provider: String,
        @Query("redirect_uri") redirect: String,
        @Body code: Code
    ): JWT

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

    @GET("profile/")
    suspend fun currentProfile(): Profile

    @GET("profile/list/")
    suspend fun allProfiles(): ProfileList

    @GET("upload/blobstore/times/")
    suspend fun times(): TimesWrapper

    companion object {
        val BASE_URL = "https://api.hackillinois.org/"
    }
}
