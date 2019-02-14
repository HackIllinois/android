package org.hackillinois.android.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.hackillinois.android.R;

public class IndoorMapsFragment extends Fragment {

    private int index;

    public static final int DCL = 0;
    public static final int ECEB = 1;
    public static final int SIEBEL = 2;
    public static final int GYM = 3;

    ImageView map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor_maps, container, false);

        TabLayout tabs = view.findViewById(R.id.indoor_tabs);
        tabs.addOnTabSelectedListener(new TabListener());
        index = 0;

        map = view.findViewById(R.id.map);
        map.setImageResource(R.drawable.ic_dcl_indoor);
        map.setAdjustViewBounds(true);

        return view;
    }


    private class TabListener implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            index = tab.getPosition();

            switch(index) {
                case DCL:
                    map.setImageResource(android.R.color.transparent);
                    map.setImageDrawable(getResources().getDrawable(R.drawable.ic_dcl_indoor));
                    break;
                case ECEB:
                    map.setImageResource(android.R.color.transparent);
                    map.setImageDrawable(getResources().getDrawable(R.drawable.ic_eceb_indoor));
                    break;
                case SIEBEL:
                    map.setImageResource(android.R.color.transparent);
                    map.setImageDrawable(getResources().getDrawable(R.drawable.ic_siebel_indoor));
                    break;
            }

        }

        public void onTabUnselected(TabLayout.Tab tab) {
            map.setImageResource(android.R.color.transparent);
        }

        public void onTabReselected(TabLayout.Tab tab) {
            index = tab.getPosition();
        }
    }
}

