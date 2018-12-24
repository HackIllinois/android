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
import java.util.List;

import org.hackillinois.android.R;
import org.hackillinois.android.model.Event;
import org.hackillinois.android.model.EventsList;
import org.hackillinois.android.viewmodel.HomeViewModel;

public class ScheduleFragment extends Fragment {

    // TODO: these aren't in the right timezone?
    final static long FRIDAY_END = Timestamp.valueOf("2019-02-23 00:00:00").getTime();
    final static long SATURDAY_END = Timestamp.valueOf("2019-02-24 00:00:00").getTime();
    final static long SUNDAY_END = Timestamp.valueOf("2019-02-25 00:00:00").getTime();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    private static List<Event> mEventList;
    private ViewPager mViewPager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        final TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        HomeViewModel viewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        viewModel.getEventsListLiveData().observe(this, new Observer<EventsList>() {
            @Override
            public void onChanged(@Nullable EventsList eventsList) {
                mEventList = eventsList.getEvents();
                // TODO: include loading bar?
                mViewPager.setAdapter(mSectionsPagerAdapter);
            }
        });

        viewModel.fetchEvents();

        return view;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DayFragment extends Fragment {

        private static final String ARG_SECTION_DATE = "section_date";
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DayFragment newInstance(int sectionNumber) {
            DayFragment fragment = new DayFragment();
            Bundle args = new Bundle();

            long date = 0;
            switch (sectionNumber) {
                case 0:
                    date = FRIDAY_END;
                    break;
                case 1:
                    date = SATURDAY_END;
                    break;
                case 2:
                    date = SUNDAY_END;
            }

            args.putLong(ARG_SECTION_DATE, date);
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_schedule_fragment, container, false);


            long date = getArguments() != null ? getArguments().getLong(ARG_SECTION_DATE) : 0;
            if (date == 0) {
                throw new IllegalArgumentException();
            }
            // TODO: filter out events by date

            final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.activity_schedule_recyclerview);
            mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);

            // and then filter the mEventList to the date
            // need to pass eventlist to dayfragment class
            // sort events by date and then separate them into each day; you're already given the date

            mAdapter = new EventsAdapter(mEventList);
            recyclerView.setAdapter(mAdapter);

            return view;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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
