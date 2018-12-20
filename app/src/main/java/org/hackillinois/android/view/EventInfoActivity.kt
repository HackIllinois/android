package org.hackillinois.android.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_event_info.*
import org.hackillinois.android.R
import org.hackillinois.android.model.Event
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.common.DirectionsOnClickListener

class EventInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val event = intent?.extras?.get("event") as Event

        eventTitle.text = event.name
        eventLocation.text = event.locationDescription
        eventDescription.text = event.description

        val location = LatLng(event.latitude, event.longitude)
        directionsButton.setOnClickListener(DirectionsOnClickListener(location, event.name))
    }
}
