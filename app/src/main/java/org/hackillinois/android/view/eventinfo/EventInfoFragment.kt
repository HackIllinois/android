package org.hackillinois.android.view.eventinfo

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.event_tile.*
import kotlinx.android.synthetic.main.fragment_event_info.*
import kotlinx.android.synthetic.main.fragment_event_info.view.*
import kotlinx.android.synthetic.main.fragment_schedule_popout.*
import kotlinx.android.synthetic.main.fragment_schedule_popout.view.*
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.EventInfoViewModel

class EventInfoFragment : Fragment() {
    private lateinit var viewModel: EventInfoViewModel

    private val FIFTEEN_MINUTES_IN_MS = 1000 * 60 * 15
    private lateinit var eventId: String
    private lateinit var eventName: String

    private var event: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventId = arguments?.getString(EVENT_ID_KEY) ?: ""

        viewModel = ViewModelProviders.of(this).get(EventInfoViewModel::class.java)
        viewModel.init(eventId)
        viewModel.event.observe(this, Observer { updateEventUI(it) })
//        viewModel.roles.observe(this, Observer { updateCameraIcon(it) })

        viewModel.isFavorited.observe(this, Observer { updateFavoritedUI(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_popout, container, false)

        view.exit_button.setOnClickListener { activity?.onBackPressed() }
        view.favorite_project.setOnClickListener {
            viewModel.changeFavoritedState()
        }

        return view
    }

    private fun updateEventUI(event: Event?) {
        event?.let {
            this.event = it
            this.eventName = it.name
            event_name.text = it.name
//            pointsView.text = "${it.points} Points!"
            event_sponsor.text = "Sponsored by ${event.sponsor}"
            event_sponsor.visibility = if (event.sponsor.isEmpty()) View.GONE else View.VISIBLE
            event_time.text = "${it.getStartTimeOfDay()} - ${it.getEndTimeOfDay()}"
//            eventLocation.text = it.getLocationDescriptionsAsString()
            event_description.text = it.description

//            val timeUntil = it.getStartTimeMs() - System.currentTimeMillis()
//            if (timeUntil > 0 && timeUntil <= FIFTEEN_MINUTES_IN_MS) {
//                happeningSoonTextView.visibility = View.VISIBLE
//            } else {
//                happeningSoonTextView.visibility = View.INVISIBLE
//            }

//            context?.let { context ->
//                mapsWithDirectionsListView.adapter = MapsWithDirectionsAdapter(context, it.getIndoorMapAndDirectionInfo())
//            }
        }
    }

//    private fun updateCameraIcon(roles: Roles?) = roles?.let {
//        if (it.isStaff()) {
//            cameraButton.visibility = View.VISIBLE
//            cameraButton.setOnClickListener {
//                if (event?.isCurrentlyHappening() == true) {
//                    val scannerFragment = ScannerFragment.newInstance(eventId, eventName)
//                    (activity as MainActivity?)?.switchFragment(scannerFragment, true)
//                } else {
//                    AlertDialog.Builder(context)
//                            .setTitle("Scanning Confirmation")
//                            .setMessage("Event is not in progress. Are you sure you want to scan?")
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .setPositiveButton(android.R.string.yes) { dialogInterface: DialogInterface, _: Int ->
//                                dialogInterface.dismiss()
//                                val scannerFragment = ScannerFragment.newInstance(eventId, eventName)
//                                (activity as MainActivity?)?.switchFragment(scannerFragment, true)
//                            }
//                            .setNegativeButton(android.R.string.no, null).show()
//                }
//            }
//        } else {
//            cameraButton.visibility = View.GONE
//        }
//    }

    private fun updateFavoritedUI(isFavorited: Boolean?) {
        isFavorited?.let {
            favorite_project.isSelected = isFavorited
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
