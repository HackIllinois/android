package org.hackillinois.android.view.schedule;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import org.hackillinois.android.R;
import org.hackillinois.android.database.entity.Event;
import org.hackillinois.android.viewmodel.ScheduleViewModel;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ViewPager viewPager = view.findViewById(R.id.container);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        return view;
    }


    public static class DayFragment extends Fragment {
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
                    Collections.sort(sortedEvents);
                    // TODO: move sorting out of here

                    adapter = new EventsAdapter(sortedEvents);
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_schedule_fragment, container, false);

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


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return DayFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
