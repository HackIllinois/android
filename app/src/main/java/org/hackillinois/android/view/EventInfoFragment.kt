package org.hackillinois.android.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoFragment : Fragment() {
    private lateinit var viewModel: EventInfoViewModel

    private val FIFTEEN_MINUTES_IN_MS = 1000 * 60 * 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val eventName = arguments?.getString("event_name") ?: ""

        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventName)
        viewModel.event.observe(this, Observer { updateEventUI(it) })

        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_info, container, false)

        view.closeButton.setOnClickListener { activity?.onBackPressed() }
        view.favoriteButton.setOnClickListener {
            viewModel.changeFavoritedState()
        }

        return view
    }


    private fun updateEventUI(event: Event?) {
        event?.let {
            eventTitle.text = it.name
            eventTimeSpan.text = "${it.getStartTimeOfDay()} - ${it.getEndTimeOfDay()}"
            eventLocation.text = it.getLocationDescriptionsAsString()
            eventDescription.text = it.description

            val timeUntil = it.getStartTimeMs() - System.currentTimeMillis()
            if (timeUntil > 0 && timeUntil <= FIFTEEN_MINUTES_IN_MS) {
                happeningSoonTextView.visibility = View.VISIBLE
            } else {
                happeningSoonTextView.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            favoriteButton.isSelected = isFavorited
        }
    }

    companion object {
        val EVENT_NAME_KEY = "event_name"

        fun newInstance(eventName: String): EventInfoFragment {
            val fragment = EventInfoFragment()
            val args = Bundle().apply {
                putString(EVENT_NAME_KEY, eventName)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
