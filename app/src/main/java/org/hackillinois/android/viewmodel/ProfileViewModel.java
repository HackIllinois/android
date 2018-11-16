package org.hackillinois.android.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hackillinois.android.App;
import org.hackillinois.android.model.Attendee;
import org.hackillinois.android.model.QR;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<QR> qr = new MutableLiveData<>();
    private MutableLiveData<Attendee> attendee = new MutableLiveData<>();

    public void fetchQR() {
        App.getAPI().getQRCode().enqueue(new Callback<QR>() {
            @Override
            public void onResponse(Call<QR> call, Response<QR> response) {
                qr.postValue(response.body());
            }

            @Override
            public void onFailure(Call<QR> call, Throwable t) {}
        });
    }

    public void fetchAttendee() {
        App.getAPI().getAttendee().enqueue(new Callback<Attendee>() {
            public void onResponse(Call<Attendee> call, Response<Attendee> response) {
                attendee.postValue(response.body());
            }

            public void onFailure(Call<Attendee> call, Throwable t) {}
        });
    }

    public MutableLiveData<QR> getQR() {
        return qr;
    }

    public MutableLiveData<Attendee> getAttendee() {
        return attendee;
    }
}
