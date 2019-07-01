package org.hackillinois.android.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_event_info.*
import org.hackillinois.android.R
import org.hackillinois.android.common.DirectionsOnClickListener
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoActivity : AppCompatActivity() {
    private lateinit var viewModel: EventInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)

        val eventName = intent?.getStringExtra("event_name") ?: ""

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow)
            title = getString(R.string.event_info_title)
        }

        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventName)
        viewModel.event.observe(this, Observer { updateEventUI(it) })

        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
        favoriteButton.setOnClickListener {
            viewModel.changeFavoritedState()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    private fun updateEventUI(event: Event?) {
        event?.let {
            eventTitle.text = it.name
            eventStartTime.text = it.getStartTimeOfDay()
            eventEndTime.text = it.getEndTimeOfDay()
            eventLocation.text = it.getLocationDescriptionsAsString()
            eventDescription.text = it.description

            if (it.locations.isEmpty()) {
                directionsButton.visibility = View.GONE
            } else {
                directionsButton.visibility = View.VISIBLE
                val location = LatLng(it.locations[0].latitude, it.locations[0].longitude)
                directionsButton.setOnClickListener(DirectionsOnClickListener(location, event.name))
            }

        }
    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            favoriteButton.isSelected = isFavorited
        }
    }
}
