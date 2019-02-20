package org.hackillinois.androidapp2019;

import org.hackillinois.androidapp2019.database.entity.Attendee;
import org.hackillinois.androidapp2019.database.entity.Event;
import org.hackillinois.androidapp2019.database.entity.QR;
import org.hackillinois.androidapp2019.database.entity.Roles;
import org.hackillinois.androidapp2019.database.entity.User;
import org.hackillinois.androidapp2019.model.auth.Code;
import org.hackillinois.androidapp2019.model.auth.JWT;
import org.hackillinois.androidapp2019.model.checkin.CheckIn;
import org.hackillinois.androidapp2019.model.event.EventsList;
import org.hackillinois.androidapp2019.model.event.TrackerContainer;
import org.hackillinois.androidapp2019.model.event.UserEventPair;
import org.hackillinois.androidapp2019.model.notification.Notification;
import org.hackillinois.androidapp2019.model.notification.NotificationTopics;
import org.hackillinois.androidapp2019.notifications.DeviceToken;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface API {
    String BASE_URL = "https://api.hackillinois.org/";

    @GET("event/{name}/")
    Call<Event> getEvent(@Path("name") String name);

    @GET("event/")
    Call<EventsList> getAllEvents();

    @GET("registration/attendee/")
    Call<Attendee> getAttendee();

    @GET("user/")
    Call<User> getUser();

    @GET("user/qr/")
    Call<QR> getQRCode();

    @POST("auth/code/{provider}/")
    Call<JWT> getJWT(@Path("provider") String provider, @Query("redirect_uri") String redirect, @Body Code code);

    @POST("notifications/device/")
    Call<DeviceToken> sendUserToken(@Body DeviceToken token);

    @GET("auth/roles/")
    Call<Roles> getRoles();

    @GET("stat/")
    Call<ResponseBody> getStats();

    @POST("event/")
    Call<Event> createEvent(@Body Event event);

    @GET("notifications/")
    Call<NotificationTopics> getNotificationTopics();

    @POST("notifications/{topic}/")
    Call<Notification> createNotification(@Path("topic") String topic, @Body Notification notification);

    @POST("event/track/")
    Call<TrackerContainer> markUserAsAttendingEvent(@Body UserEventPair userEventPair);

    @POST("checkin/")
    Call<CheckIn> checkInUser(@Body CheckIn checkIn);

    @POST("notifications/update/")
    Call<NotificationTopics> updateNotificationTopics();
}
