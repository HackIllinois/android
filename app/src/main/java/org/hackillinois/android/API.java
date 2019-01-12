package org.hackillinois.android;

import org.hackillinois.android.database.entity.Attendee;
import org.hackillinois.android.database.entity.Event;
import org.hackillinois.android.model.EventsList;
import org.hackillinois.android.database.entity.QR;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface API {
    String BASE_URL = "https://api.hackillinois.org/";

    @GET("event/{name}/")
    Call<Event> getEvent(@Path("name") String name);

    @GET("event/")
    Call<EventsList> getAllEvents();

    @GET("registration/attendee/")
    Call<Attendee> getAttendee();

    @GET("user/qr/")
    Call<QR> getQRCode();
}
