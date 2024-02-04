package org.hackillinois.android

import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.EventId
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.leaderboard.LeaderboardList
import org.hackillinois.android.model.profile.Ranking
import org.hackillinois.android.model.user.FavoritesResponse
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
    suspend fun checkInUser(@Body checkIn: CheckIn): CheckIn

    // EVENT

    @GET("event/")
    suspend fun allEvents(): EventsList

    @GET("event/{id}/")
    fun getEvent(@Path("id") id: String): Call<Event>

    @POST("event/checkin/")
    suspend fun eventCheckIn(@Body id: EventCode): AttendeeCheckInResponse

    @POST("event/staff/checkin/")
    suspend fun staffEventCheckIn(@Body pair: UserEventPair): StaffCheckInResponse

    // NOTIFICATIONS

    @POST("notifications/device/")
    suspend fun sendUserToken(@Body token: DeviceToken): DeviceToken

    // PROFILE

    @GET("profile/")
    suspend fun currentProfile(): Profile

    @GET("profile/leaderboard/?limit=10")
    suspend fun leaderboard(): LeaderboardList

    @GET("profile/ranking")
    suspend fun profileRanking(): Ranking

    // REGISTRATION

    @GET("registration/attendee/")
    suspend fun attendee(): Attendee

    // SHOP

    @GET("shop/")
    suspend fun shop(): List<ShopItem>

    // STAFF

    @POST("staff/attendance/")
    suspend fun staffMeetingCheckIn(@Body eventId: MeetingEventId): MeetingCheckInResponse

    // USER

    @GET("user/")
    suspend fun user(): User

    @PUT("user/follow/")
    suspend fun favoriteEvents(): FavoritesResponse

    @PUT("user/follow/")
    fun followEvent(@Body eventId: EventId): Call<FavoritesResponse>

    @PUT("user/unfollow/")
    fun unfollowEvent(@Body eventId: EventId): Call<FavoritesResponse>

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
