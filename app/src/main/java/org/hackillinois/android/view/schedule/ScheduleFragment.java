package org.hackillinois.android.view.schedule;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hackillinois.android.R;
import org.hackillinois.android.model.Event;
import org.hackillinois.android.viewmodel.ScheduleViewModel;

public class ScheduleFragment extends Fragment {

    ScheduleViewModel mViewModel;
    SectionsPagerAdapter mSectionsPagerAdapter;
    private static ArrayList<ArrayList<Event>> sortedEvents;

    final static long FRIDAY_END = Timestamp.valueOf("2019-02-23 00:00:00").getTime();
    final static long SATURDAY_END = Timestamp.valueOf("2019-02-24 00:00:00").getTime();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ScheduleViewModel.class);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        final ViewPager mViewPager = (ViewPager) view.findViewById(R.id.container);
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mViewModel.getEventsListLiveData().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(@Nullable List<Event> events) {
                sortedEvents = new ArrayList<>();

                for (int i = 0; i < 3; i++) {
                    sortedEvents.add(new ArrayList<Event>());
                }

                for (Event event : events) {
                    if (event.getStartTime() < FRIDAY_END) {
                        sortedEvents.get(0).add(event);
                    } else if (event.getStartTime() < SATURDAY_END) {
                        sortedEvents.get(1).add(event);
                    } else {
                        sortedEvents.get(2).add(event);
                    }
                }

                for (ArrayList<Event> arrayList : sortedEvents) {
                    Collections.sort(arrayList);
                }

                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
        });

        mViewModel.fetchEvents();

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        return view;
    }


    public static class DayFragment extends Fragment {
        private static final String ARG_SECTION_NUM = "section_number";

        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

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
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_schedule_fragment, container, false);

            int sectionNumber = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUM) : 0;

            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.activity_schedule_recyclerview);
            mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new EventsAdapter(sortedEvents.get(sectionNumber));
            recyclerView.setAdapter(mAdapter);

            return view;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
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
