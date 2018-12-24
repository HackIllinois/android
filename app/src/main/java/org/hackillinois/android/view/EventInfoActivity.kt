package org.hackillinois.android.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_event_info.*
import org.hackillinois.android.R
import org.hackillinois.android.model.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.common.DirectionsOnClickListener

class EventInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: EventInfoViewModel
    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        event = intent?.extras?.get("event") as Event

        eventTitle.text = event.name
        eventLocation.text = event.locationDescription
        eventDescription.text = event.description

        val location = LatLng(event.latitude, event.longitude)
        directionsButton.setOnClickListener(DirectionsOnClickListener(location, event.name))

        favoriteEventImageView.setOnClickListener {
            viewModel.changeFavoritedState(event)
        }

        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.isFavorited.observe(this, Observer { updateFavoritedUi(it) })
        viewModel.getIsFavorited(event)
    }

    private fun updateFavoritedUi(isFavorited: Boolean?) {
        isFavorited?.let {
            if (isFavorited) {
                favoriteEventImageView.setImageResource(R.drawable.ic_full_star)
            } else {
                favoriteEventImageView.setImageResource(R.drawable.ic_hollow_star)
            }
        }
    }
}
