package org.hackillinois.android

import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.TimesWrapper
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.leaderboard.LeaderboardList
import org.hackillinois.android.model.profile.ProfilePoints
import org.hackillinois.android.model.shop.ItemInstance
import org.hackillinois.android.model.version.Version
import org.hackillinois.android.notifications.DeviceToken
import retrofit2.Call
import retrofit2.http.*

interface API {

    // AUTH

    @GET("auth/roles/")
    suspend fun roles(): Roles

    /* Note: the endpoint @GET("auth/login/{provider}/") is not called in this file
        because the URL is directly accessed in LoginActivity.kt to redirect to OAuth */

    // CHECK-IN

    @POST("checkin/")
    suspend fun checkInUser(@Body body: CheckIn): CheckIn

    // EVENT

    @GET("event/")
    suspend fun allEvents(): EventsList

    @GET("event/{id}/")
    fun getEvent(@Path("id") id: String): Call<Event>

    @POST("event/checkin/")
    suspend fun eventCheckIn(@Body body: EventCode): EventCheckInResponse

    @POST("event/staff/checkin/")
    suspend fun attendeeCheckIn(@Body body: UserEventPair): AttendeeCheckInResponse

    // NOTIFICATIONS

    @POST("notifications/device/")
    suspend fun sendUserToken(@Body body: DeviceToken): DeviceToken

    // PROFILE

    @GET("profile/")
    suspend fun currentProfile(): Profile

    @POST("profile/addpoints/")
    suspend fun addPoints(@Body body: ProfilePoints): Profile

    @GET("profile/leaderboard/?limit=10")
    suspend fun leaderboard(): LeaderboardList

    // REGISTRATION

    @GET("registration/attendee/")
    suspend fun attendee(): Attendee

    // SHOP

    @POST("shop/item/buy/")
    suspend fun buyShopItem(@Body body: ItemInstance)

    // STAFF

    @POST("staff/attendance/")
    suspend fun staffMeetingCheckIn(@Body body: MeetingEventId)

    // UPLOAD

    @GET("upload/blobstore/times/")
    suspend fun times(): TimesWrapper

    // USER

    @GET("user/")
    suspend fun user(): User

    @GET("user/qr/")
    suspend fun qrCode(): QR

    // VERSION

    @GET("version/android/")
    suspend fun versionCode(): Version

    // BASE URL

    companion object {
        val BASE_URL = "https://adonix.hackillinois.org/"
    }
}
