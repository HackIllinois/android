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
import org.hackillinois.android.model.notification.Notification
import org.hackillinois.android.model.notification.NotificationTopics
import org.hackillinois.android.notifications.DeviceToken

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface API {

    @GET("event/")
    fun allEvents(): Call<EventsList>

    @GET("registration/attendee/")
    fun attendee(): Call<Attendee>

    @GET("user/")
    fun user(): Call<User>

    @GET("user/qr/")
    fun qrCode(): Call<QR>

    @GET("auth/roles/")
    fun roles(): Call<Roles>

    @GET("stat/")
    fun stats(): Call<ResponseBody>

    @GET("notifications/")
    fun notificationTopics(): Call<NotificationTopics>

    @GET("event/{name}/")
    fun getEvent(@Path("name") name: String): Call<Event>

    @POST("auth/code/{provider}/")
    fun getJWT(@Path("provider") provider: String, @Query("redirect_uri") redirect: String, @Body code: Code): Call<JWT>

    @POST("notifications/device/")
    fun sendUserToken(@Body token: DeviceToken): Call<DeviceToken>

    @POST("event/")
    fun createEvent(@Body event: Event): Call<Event>

    @POST("notifications/{topic}/")
    fun createNotification(@Path("topic") topic: String, @Body notification: Notification): Call<Notification>

    @POST("event/track/")
    fun markUserAsAttendingEvent(@Body userEventPair: UserEventPair): Call<TrackerContainer>

    @POST("checkin/")
    fun checkInUser(@Body checkIn: CheckIn): Call<CheckIn>

    @POST("notifications/update/")
    fun updateNotificationTopics(): Call<NotificationTopics>

    companion object {
        val BASE_URL = "https://api.hackillinois.org/"
    }
}
