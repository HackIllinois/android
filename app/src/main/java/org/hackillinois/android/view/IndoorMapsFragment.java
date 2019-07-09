package org.hackillinois.android.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.hackillinois.android.R;

public class IndoorMapsFragment extends Fragment {
    private static final int DCL = 0;
    private static final int ECEB = 1;
    private static final int SIEBEL = 2;

    private SubsamplingScaleImageView map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor_maps, container, false);

        TabLayout tabs = view.findViewById(R.id.indoor_tabs);
        tabs.addOnTabSelectedListener(new TabListener());

        map = view.findViewById(R.id.map);
        map.setMinimumTileDpi(160);
        setImage(DCL);

        return view;
    }


    private class TabListener implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            int index = tab.getPosition();
            setImage(index);
        }

        public void onTabUnselected(TabLayout.Tab tab) {
        }

        public void onTabReselected(TabLayout.Tab tab) {
        }
    }

    private void setImage(int index) {
        switch (index) {
            case DCL:
                map.setImage(ImageSource.resource(R.drawable.dcl_indoor));
                break;
            case ECEB:
                map.setImage(ImageSource.resource(R.drawable.eceb_indoor));
                break;
            case SIEBEL:
                map.setImage(ImageSource.resource(R.drawable.siebel_indoor));
                break;
        }
    }
}

