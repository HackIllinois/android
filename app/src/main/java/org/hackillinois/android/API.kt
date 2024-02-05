package org.hackillinois.android

import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.event.DietaryRestrictions
import org.hackillinois.android.model.event.EventId
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.event.MentorId
import org.hackillinois.android.model.event.Points
import org.hackillinois.android.model.leaderboard.LeaderboardList
import org.hackillinois.android.model.profile.ProfilePoints
import org.hackillinois.android.model.scanner.UserEventIds
import org.hackillinois.android.model.shop.ItemInstance
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

    // EVENT

    @GET("event/")
    suspend fun allEvents(): EventsList

//    @POST("event/checkin/")
//    suspend fun eventCheckIn(@Body body: EventCode): EventCheckInResponse // todo: changed to user/scan-event/

//    @POST("event/staff/checkin/")
//    suspend fun attendeeCheckIn(@Body body: UserEventPair): AttendeeCheckInResponse // todo: changed to staff/scan-attendee/

    // MENTOR

    @POST("mentor/attendance/")
    suspend fun scanMentor(@Body body: MentorId): Points

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

    @GET("shop/")
    suspend fun shop(): List<ShopItem>

    @POST("shop/item/buy/")
    suspend fun buyShopItem(@Body body: ItemInstance)

    // STAFF

    @POST("staff/attendance/")
    suspend fun staffMeetingCheckIn(@Body body: MeetingEventId)

    @PUT("staff/scan-attendee/")
    suspend fun scanAttendee(@Body body: UserEventIds): DietaryRestrictions

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

    @PUT("user/scan-event/")
    suspend fun scanEvent(@Body body: EventId): Points

    // VERSION

    @GET("version/android/")
    suspend fun versionCode(): Version

    // BASE URL

    companion object {
        val BASE_URL = "https://adonix.hackillinois.org/"
    }
}
