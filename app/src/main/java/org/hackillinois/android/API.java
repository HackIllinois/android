package org.hackillinois.android;

import org.hackillinois.android.database.entity.Attendee;
import org.hackillinois.android.database.entity.Event;
import org.hackillinois.android.database.entity.Roles;
import org.hackillinois.android.database.entity.User;
import org.hackillinois.android.firebase.DeviceToken;
import org.hackillinois.android.model.EventsList;
import org.hackillinois.android.database.entity.QR;
import org.hackillinois.android.model.JWT;
import org.hackillinois.android.model.Code;

import java.util.List;

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
}
