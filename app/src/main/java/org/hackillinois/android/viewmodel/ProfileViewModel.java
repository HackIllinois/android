package org.hackillinois.android.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.hackillinois.android.App;
import org.hackillinois.android.model.Attendee;
import org.hackillinois.android.database.entity.QR;
import org.hackillinois.android.repository.QRRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private LiveData<QR> qr;
    private MutableLiveData<Attendee> attendee = new MutableLiveData<>();

    private QRRepository qrRepository;

    public void init() {
        qrRepository = QRRepository.Companion.getInstance();
        qr = qrRepository.fetchQR();
    }

    public void fetchAttendee() {
        App.getAPI().getAttendee().enqueue(new Callback<Attendee>() {
            public void onResponse(Call<Attendee> call, Response<Attendee> response) {
                attendee.postValue(response.body());
            }

            public void onFailure(Call<Attendee> call, Throwable t) {}
        });
    }

    public LiveData<QR> getQR() {
        return qr;
    }

    public MutableLiveData<Attendee> getAttendee() {
        return attendee;
    }
}
