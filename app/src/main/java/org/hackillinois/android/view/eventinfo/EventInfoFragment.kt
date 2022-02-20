package org.hackillinois.android.view.eventinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoFragment : Fragment() {
    private lateinit var viewModel: EventInfoViewModel

    private val FIFTEEN_MINUTES_IN_MS = 1000 * 60 * 15
    private lateinit var eventId: String
    private lateinit var eventName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""
        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventId)
        viewModel.event.observe(this, Observer { updateEventUI(it) })
        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_event_info, container, false)
        view.exit_button.setOnClickListener { activity?.onBackPressed() }
        view.favorites_button.setOnClickListener {
            viewModel.changeFavoritedState()
        }
        return view
    }

    private fun updateEventUI(event: Event?) {
        event?.let {
            event_name.text = it.name
            event_points.text = "${it.points} Points!"
            event_sponsor.text = "Sponsored by ${it.sponsor}"
            event_sponsor.visibility = if (it.sponsor.isEmpty()) View.GONE else View.VISIBLE
            event_time.text = "${it.getStartTimeOfDay()} - ${it.getEndTimeOfDay()}"
            event_time.visibility = if (it.isAsync) View.GONE else View.VISIBLE
            event_description.text = it.description
        }
    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            exit_button.isSelected = isFavorited
            val imageResource = if (isFavorited) R.drawable.ic_star_filled else R.drawable.ic_star_border
            favorites_button.setImageResource(imageResource)
        }
    }

    companion object {
        val EVENT_ID_KEY = "event_id"

        fun newInstance(eventId: String): EventInfoFragment {
            val fragment = EventInfoFragment()
            val args = Bundle().apply {
                putString(EVENT_ID_KEY, eventId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
