package org.hackillinois.android.view;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hackillinois.android.R;
import org.hackillinois.android.model.Attendee;
import org.hackillinois.android.viewmodel.ProfileViewModel;

public class ProfileFragment extends Fragment {
    private TextView nameTextView;
    private TextView dietaryRestrictionsTextView;
    private TextView universityTextView;
    private TextView majorTextView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProfileViewModel viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        viewModel.getAttendee().observe(this, new Observer<Attendee>() {
            public void onChanged(Attendee attendee) {
                nameTextView.setText(attendee.getName());
                dietaryRestrictionsTextView.setText(attendee.getDiet());
                universityTextView.setText(attendee.getSchool());
                majorTextView.setText(attendee.getMajor());
            }
        });

        viewModel.fetchAttendee();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = view.findViewById(R.id.name);
        dietaryRestrictionsTextView = view.findViewById(R.id.dietaryRestrictions);
        universityTextView = view.findViewById(R.id.university);
        majorTextView = view.findViewById(R.id.major);

        return view;
    }
}
