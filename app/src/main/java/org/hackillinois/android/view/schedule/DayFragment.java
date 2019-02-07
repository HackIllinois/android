package org.hackillinois.android.view.schedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hackillinois.android.R;
import org.hackillinois.android.database.entity.Event;
import org.hackillinois.android.viewmodel.ScheduleViewModel;

import java.util.List;

public class DayFragment extends Fragment {
    private static final String ARG_SECTION_NUM = "section_number";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Event> sortedEvents;

    public static DayFragment newInstance(int sectionNumber) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUM, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int sectionNumber = getArguments() == null ? 0 : getArguments().getInt(ARG_SECTION_NUM);

        ScheduleViewModel viewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        viewModel.init();

        LiveData<List<Event>> liveData;

        switch (sectionNumber) {
            case 0: liveData = viewModel.getFridayEventsLiveData();
                break;
            case 1: liveData = viewModel.getSaturdayEventsLiveData();
                break;
            default: liveData = viewModel.getSundayEventsLiveData();
        }

        liveData.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                sortedEvents = events;
                adapter = new EventsAdapter(sortedEvents);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_day, container, false);

        recyclerView = view.findViewById(R.id.activity_schedule_recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        if (sortedEvents != null) {
            adapter = new EventsAdapter(sortedEvents);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}