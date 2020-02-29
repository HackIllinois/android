package org.hackillinois.android

import org.hackillinois.android.database.entity.Attendee
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.QR
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.database.entity.User
import org.hackillinois.android.model.auth.Code
import org.hackillinois.android.model.auth.JWT
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.event.TrackerContainer
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.model.notification.NotificationTopics
import org.hackillinois.android.notifications.DeviceToken

import okhttp3.ResponseBody
import org.hackillinois.android.model.TimesWrapper
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
    fun getJWT(@Path("provider") provider: String, @Query("redirect_uri") redirect: String, @Body code: Code): Call<JWT>

    @GET("auth/roles/")
    fun roles(): Call<Roles>

    // CHECK-IN

    @POST("checkin/")
    fun checkInUser(@Body checkIn: CheckIn): Call<CheckIn>

    // EVENT

    @GET("event/")
    fun allEvents(): Call<EventsList>

    @POST("event/")
    fun createEvent(@Body event: Event): Call<Event>

    @GET("event/{id}/")
    fun getEvent(@Path("id") id: String): Call<Event>

    @POST("event/track/")
    fun markUserAsAttendingEvent(@Body userEventPair: UserEventPair): Call<TrackerContainer>

    // NOTIFICATIONS

    @POST("notifications/device/")
    fun sendUserToken(@Body token: DeviceToken): Call<DeviceToken>

    @POST("notifications/update/")
    fun updateNotificationTopics(): Call<NotificationTopics>

    // REGISTRATION

    @GET("registration/attendee/")
    fun attendee(): Call<Attendee>

    // STAT

    @GET("stat/")
    fun stats(): Call<ResponseBody>

    // USER

    @GET("user/")
    fun user(): Call<User>

    @GET("user/qr/")
    fun qrCode(): Call<QR>

    // PROJECT

    @GET("project/")
    fun allProjects(): Call<ProjectsList>

    @GET("upload/blobstore/times/")
    fun times(): Call<TimesWrapper>

    companion object {
        val BASE_URL = "https://api.hackillinois.org/"
    }
}
