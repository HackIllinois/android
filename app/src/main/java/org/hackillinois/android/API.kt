package org.hackillinois.android

import org.hackillinois.android.database.entity.*
import org.hackillinois.android.model.checkin.CheckIn
import org.hackillinois.android.model.event.AttendeeCheckInResponse
import org.hackillinois.android.model.event.EventCode
import org.hackillinois.android.model.event.EventId
import org.hackillinois.android.model.event.EventsList
import org.hackillinois.android.model.event.MeetingCheckInResponse
import org.hackillinois.android.model.event.MeetingEventId
import org.hackillinois.android.model.event.ShiftsList
import org.hackillinois.android.model.event.StaffCheckInResponse
import org.hackillinois.android.model.event.UserEventPair
import org.hackillinois.android.model.leaderboard.LeaderboardList
import org.hackillinois.android.model.profile.Ranking
import org.hackillinois.android.model.scanner.DietaryRestrictions
import org.hackillinois.android.model.scanner.EventId
import org.hackillinois.android.model.scanner.MentorId
import org.hackillinois.android.model.scanner.Points
import org.hackillinois.android.model.scanner.UserEventPair
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

    @GET("staff/shift/")
    suspend fun allShifts(): ShiftsList

    @GET("event/{id}/")
    fun getEvent(@Path("id") id: String): Call<Event>

    @POST("event/checkin/")
    suspend fun eventCheckIn(@Body id: EventCode): AttendeeCheckInResponse

    // MENTOR

    @POST("mentor/attendance/")
    suspend fun scanMentor(@Body body: MentorId): Points

    // NOTIFICATIONS

    @POST("notification/")
    suspend fun sendNotificationToken(@Body body: DeviceToken)

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

    @POST("shop/item/buy/")
    suspend fun buyShopItem(@Body body: ItemInstance)

    // STAFF

    @POST("staff/attendance/")
    suspend fun staffMeetingCheckIn(@Body body: EventId)

    @PUT("staff/scan-attendee/")
    suspend fun scanAttendee(@Body body: UserEventPair): DietaryRestrictions

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
