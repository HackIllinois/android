package org.hackillinois.android2019.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.hackillinois.android2019.R;
import org.hackillinois.android2019.common.DirectionsOnClickListener;

public class OutdoorMapsFragment extends Fragment implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 0;
    private static final int ZOOM = 18;

    private static final String[] BUILDING_NAMES = {
            "Digital Computing Laboratory",
            "Electrical and Computer Engineering Building",
            "Thomas M. Siebel Center for Computer Science",
            "Kenney Gym"
    };
    private static final LatLng[] BUILDING_LOCATIONS = {
            new LatLng(40.1131428, -88.2265095),
            new LatLng(40.1149213, -88.2280232),
            new LatLng(40.1138194, -88.2250361),
            new LatLng(40.1130480, -88.2283660)
    };

    private int index;

    private GoogleMap map;
    private FloatingActionButton directions;
    private TextView building;
    private LinearLayout locationInfo;
    private TextView time;
    private TextView distance;

    private LocationManager locationManager;
    private UserLocation userLocation;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_outdoor_maps, container, false);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(new TabListener());
        index = 0;

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        directions = view.findViewById(R.id.directions);
        directions.setOnClickListener(new DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]));

        building = view.findViewById(R.id.building);
        building.setText(BUILDING_NAMES[index]);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationInfo = view.findViewById(R.id.locationInfo);
        time = view.findViewById(R.id.time);
        distance = view.findViewById(R.id.distance);
        getUserLocation();

        return view;
    }

    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.addMarker(new MarkerOptions().position(BUILDING_LOCATIONS[index]));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));
    }

    private void getUserLocation() {
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            return;
        }

        boolean hasPermission = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!hasPermission) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        final int ONE_MINUTE = 60 * 1000;
        userLocation = new UserLocation();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, ONE_MINUTE, 0, userLocation);
        locationInfo.setVisibility(View.VISIBLE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        }
    }

    private class TabListener implements TabLayout.OnTabSelectedListener {
        public void onTabSelected(TabLayout.Tab tab) {
            index = tab.getPosition();

            map.addMarker(new MarkerOptions().position(BUILDING_LOCATIONS[index]));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));

            directions.setOnClickListener(new DirectionsOnClickListener(BUILDING_LOCATIONS[index], BUILDING_NAMES[index]));

            building.setText(BUILDING_NAMES[index]);
            if (userLocation != null) {
                userLocation.update();
            }
        }

        public void onTabUnselected(TabLayout.Tab tab) {
            map.clear();
        }

        public void onTabReselected(TabLayout.Tab tab) {
            index = tab.getPosition();
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(BUILDING_LOCATIONS[index], ZOOM));
        }
    }

    private class UserLocation implements android.location.LocationListener {
        private LatLng location;

        public void onLocationChanged(Location loc) {
            location = new LatLng(loc.getLatitude(), loc.getLongitude());
            update();
        }

        private void update() {
            if (location == null) {
                return;
            }

            double dlat = Math.abs(BUILDING_LOCATIONS[index].latitude - location.latitude);
            double dlon = Math.abs(BUILDING_LOCATIONS[index].longitude - location.longitude);

            final int EARTH_RADIUS = 3959;
            double miles = 2.0 * Math.PI * EARTH_RADIUS * (dlat + dlon) / 360.0;
            distance.setText(String.format("%.1f miles", miles));

            final int WALKING_MPH = 3;
            final int MINUTES_IN_HOUR = 60;
            int minutes = (int) (miles / WALKING_MPH * MINUTES_IN_HOUR);
            time.setText(String.format("%d min", minutes));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }
}

