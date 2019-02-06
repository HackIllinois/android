package org.hackillinois.android.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import org.hackillinois.android.database.entity.Attendee;
import org.hackillinois.android.database.entity.QR;
import org.hackillinois.android.database.entity.User;
import org.hackillinois.android.repository.AttendeeRepository;
import org.hackillinois.android.repository.QRRepository;
import org.hackillinois.android.repository.UserRepository;

public class ProfileViewModel extends ViewModel {
    private LiveData<QR> qr;
    private LiveData<Attendee> attendee;
    private LiveData<User> user;

    private QRRepository qrRepository;
    private AttendeeRepository attendeeRepository;
    private UserRepository userRepository;

    public void init() {
        qrRepository = QRRepository.Companion.getInstance();
        attendeeRepository = AttendeeRepository.Companion.getInstance();
        userRepository = UserRepository.Companion.getInstance();
        qr = qrRepository.fetchQR();
        attendee = attendeeRepository.fetchAttendee();
        user = userRepository.fetchUser();
    }

    public LiveData<QR> getQR() {
        return qr;
    }

    public LiveData<Attendee> getAttendee() {
        return attendee;
    }

    public LiveData<User> getUser() {
        return user;
    }
}
