package org.hackillinois.android.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_event_info.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel
import com.google.android.gms.maps.model.LatLng
import org.hackillinois.android.common.DirectionsOnClickListener

class EventInfoActivity : AppCompatActivity() {

    private lateinit var viewModel: EventInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val eventName = intent?.getStringExtra("event_name") ?: ""

        favoriteEventImageView.setOnClickListener {
            viewModel.changeFavoritedState(eventName)
        }

        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventName)
        viewModel.isFavorited.observe(this, Observer { updateFavoritedUi(it) })
        viewModel.event.observe(this, Observer { updateEventUi(it) })
        viewModel.getIsFavorited(eventName)
    }

    private fun updateEventUi(event: Event?) {
        event?.let {
            eventTitle.text = it.name
            eventLocation.text = it.locationDescription
            eventDescription.text = it.description

            val location = LatLng(it.latitude, it.longitude)
            directionsButton.setOnClickListener(DirectionsOnClickListener(location, event.name))
        }
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
