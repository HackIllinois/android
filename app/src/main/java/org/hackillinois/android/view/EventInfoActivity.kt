package org.hackillinois.android.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_event_info.*
import org.hackillinois.android.R
import org.hackillinois.android.model.Event
import android.content.Intent
import android.net.Uri

class EventInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val event = intent?.extras?.get("event") as Event

        eventTitle.text = event.name
        eventLocation.text = event.locationDescription
        eventDescription.text = event.description

        directionsButton.setOnClickListener {
            val geoUri = "http://maps.google.com/maps?q=loc:${event.latitude},${event.longitude} (${event.locationDescription})"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
            startActivity(intent)
        }
    }
}
