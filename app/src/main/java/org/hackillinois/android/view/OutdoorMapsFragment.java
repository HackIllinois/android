package org.hackillinois.android.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hackillinois.android.R;
import org.hackillinois.android.common.DirectionsOnClickListener;

public class OutdoorMapsFragment extends Fragment implements OnMapReadyCallback {
    private static final String[] BUILDING_NAMES = {
            "Digital Computing Laboratory",
            "Electrical and Computer Engineering Building",
            "Thomas M. Siebel Center for Computer Science",
            "Illini Union"
    };
    private static final LatLng[] BUILDING_LOCATIONS = {
            new LatLng(40.1131428, -88.2265095),
            new LatLng(40.1149213, -88.2280232),
            new LatLng(40.1138194, -88.2250361),
            new LatLng(40.1094131, -88.2271693)
    };
    private static final int ZOOM = 18;

    private GoogleMap map;
    private int index;

    private TextView building;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outdoor_maps, container, false);

        if (mapsAvailable()) {
            TabLayout tabs = view.findViewById(R.id.tabs);
            tabs.addOnTabSelectedListener(new TabListener());

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            building = view.findViewById(R.id.building);

            FloatingActionButton directions = view.findViewById(R.id.directions);
            directions.setOnClickListener(new DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]));
        }

        return view;
    }

    private boolean mapsAvailable() {
        return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS;
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setIndoorLevelPickerEnabled(false);

        index = 0;
        map.addMarker(new MarkerOptions().position(BUILDING_LOCATIONS[index]));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));
        building.setText(BUILDING_NAMES[index]);
    }

    private class TabListener implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            index = tab.getPosition();
            map.addMarker(new MarkerOptions().position(BUILDING_LOCATIONS[index]));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));
            building.setText(BUILDING_NAMES[index]);
        }

        public void onTabUnselected(TabLayout.Tab tab) {
            map.clear();
        }

        public void onTabReselected(TabLayout.Tab tab) {
            index = tab.getPosition();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));
        }
    }
}

