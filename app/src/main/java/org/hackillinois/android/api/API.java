package org.hackillinois.android.api;

import org.hackillinois.android.model.Attendee;
import org.hackillinois.android.model.Event;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface API {
    String BASE_URL = "https://api.hackillinois.org/";

    @GET("event/{name}/")
    Call<Event> getEvent(@Path("name") String name);

    @GET("registration/attendee/")
    Call<Attendee> getAttendee();
}
