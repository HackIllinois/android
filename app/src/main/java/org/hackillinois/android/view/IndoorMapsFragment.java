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
    private static final int[] imageResources =
            new int[] {R.drawable.dcl_indoor, R.drawable.eceb_indoor, R.drawable.siebel_indoor};

    private SubsamplingScaleImageView map;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indoor_maps, container, false);

        TabLayout tabs = view.findViewById(R.id.indoor_tabs);
        tabs.addOnTabSelectedListener(new TabListener());

        map = view.findViewById(R.id.map);
        map.setMinimumTileDpi(160);
        setImage(0);

        return view;
    }

    private class TabListener implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            int index = tab.getPosition();
            setImage(index);
        }

        public void onTabUnselected(TabLayout.Tab tab) { }
        public void onTabReselected(TabLayout.Tab tab) { }
    }

    private void setImage(int index) {
        map.setImage(ImageSource.resource(imageResources[index]));
    }
}
