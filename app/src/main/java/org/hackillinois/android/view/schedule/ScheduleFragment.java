package org.hackillinois.android.view.schedule;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.hackillinois.android.R;
import org.hackillinois.android.viewmodel.ScheduleViewModel;

public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        ViewPager viewPager = view.findViewById(R.id.adminContainer);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        ScheduleViewModel scheduleViewModel = new ScheduleViewModel();

        long time = System.currentTimeMillis();

        if (time < scheduleViewModel.getFridayEnd()) {
            viewPager.setCurrentItem(0);
        } else if (time < scheduleViewModel.getSaturdayEnd()) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(2);
        }

        return view;
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
