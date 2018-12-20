package org.hackillinois.android.common;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsOnClickListener implements View.OnClickListener {
    private static final String GOOGLE_MAPS_URI = "http://maps.google.com/maps?q=loc:%s,%s (%s)";

    private LatLng location;
    private String name;

    public DirectionsOnClickListener(LatLng location, String name) {
        this.location = location;
        this.name = name;
    }

    @Override
    public void onClick(View v) {
        String uri = String.format(GOOGLE_MAPS_URI, location.latitude, location.longitude, name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        v.getContext().startActivity(intent);
    }
}
