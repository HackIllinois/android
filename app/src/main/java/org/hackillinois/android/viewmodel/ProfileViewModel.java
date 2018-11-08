package org.hackillinois.android.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hackillinois.android.App;
import org.hackillinois.android.model.Attendee;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<Attendee> attendee = new MutableLiveData<>();

    public void fetchAttendee() {
        App.getAPI().getAttendee().enqueue(new Callback<Attendee>() {
            public void onResponse(Call<Attendee> call, Response<Attendee> response) {
                attendee.postValue(response.body());
            }

            public void onFailure(Call<Attendee> call, Throwable t) {}
        });
    }

    public MutableLiveData<Attendee> getAttendee() {
        return attendee;
    }
}
