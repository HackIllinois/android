package org.hackillinois.android.view.admin

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_admin_events.*
import kotlinx.android.synthetic.main.fragment_admin_events.view.*
import kotlinx.android.synthetic.main.fragment_admin_notifications.*
import kotlinx.android.synthetic.main.fragment_admin_notifications.view.*
import kotlinx.android.synthetic.main.fragment_admin_stats.*
import kotlinx.android.synthetic.main.fragment_admin_stats.view.*
import org.hackillinois.android.R
import org.hackillinois.android.model.notification.NotificationTopics
import org.hackillinois.android.viewmodel.AdminViewModel
import java.text.ParseException
import java.text.SimpleDateFormat

class ToolsFragment : Fragment() {
    private val ARG_SECTION_NUM = "section_number"

    private lateinit var viewModel: AdminViewModel

    companion object {
        fun newInstance(sectionNumber: Int): ToolsFragment {
            val fragment = ToolsFragment()
            val args = Bundle()

            args.putInt(ToolsFragment().ARG_SECTION_NUM, sectionNumber)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(AdminViewModel::class.java)
        viewModel.stats.observe(this, Observer { updateStats(it) })
        viewModel.eventCreated.observe(this, Observer { notifiedEventCreated(it) })
        viewModel.notificationTopics.observe(this, Observer { updateNotificationTopics(it) })
        viewModel.notificationCreated.observe(this, Observer { notifiedNotificationCreated(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sectionNumber = if (arguments == null) 0 else arguments!!.getInt(ARG_SECTION_NUM)

        return when (sectionNumber) {
            0 -> createStatsView(inflater, container, savedInstanceState)
            1 -> createEventsView(inflater, container, savedInstanceState)
            else -> createNotificationsView(inflater, container, savedInstanceState)
        }
    }

    fun createStatsView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_stats, container, false)

        view.queryBtn.setOnClickListener {
            viewModel.queryStats()
        }

        return view
    }

    fun createEventsView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_events, container, false)

        view.eventCreateBtn.setOnClickListener {
            try {
                val name = eventNameInput.text.toString()
                val description = eventDescriptionInput.text.toString()
                val sponsor = eventSponsorInput.text.toString()
                val eventType = eventTypeInput.selectedItem.toString()
                val eventRoom = eventLocationRoomInput.text.toString()

                val dateParser = SimpleDateFormat("MM-dd-yyyy HH:mm")
                val startTime = dateParser.parse(eventStartInput.text.toString()).time / 1000
                val endTime = dateParser.parse(eventEndInput.text.toString()).time / 1000

                val locationName = eventLocationInput.selectedItem.toString()

                viewModel.createEvent(name, description, sponsor, eventType, eventRoom, startTime, endTime, locationName)
            } catch (e: ParseException) {
                displaySnackbar("Failed to parse date time", Snackbar.LENGTH_SHORT)
            }
        }

        return view
    }

    fun createNotificationsView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_notifications, container, false)

        viewModel.getNotificationTopics()

        view.notificationCreateBtn.setOnClickListener {
            val topic = notificationTopicInput.selectedItem.toString()
            val title = notificationTitleInput.text.toString()
            val body = notificationContentInput.text.toString()

            viewModel.createNotification(topic, title, body)
        }

        return view
    }

    private fun updateStats(stats: String?) {
        stats?.let {
            statsText.text = stats
            return
        }
        displaySnackbar("Failed to retrieve stats", Snackbar.LENGTH_SHORT)
    }

    private fun updateNotificationTopics(topics: NotificationTopics?) {
        topics?.let {
            val adapter = ArrayAdapter<String>(activity?.applicationContext!!, android.R.layout.simple_spinner_dropdown_item, it.topics)
            notificationTopicInput.adapter = adapter
            return
        }
        displaySnackbar("Failed to retrieve notification topics", Snackbar.LENGTH_SHORT)
    }

    private fun notifiedEventCreated(wasCreated: Boolean?) {
        wasCreated?.let {
            if (wasCreated) {
                displaySnackbar("Created event", Snackbar.LENGTH_SHORT)
                return
            }
        }
        displaySnackbar("Failed to create event", Snackbar.LENGTH_SHORT)
    }

    private fun notifiedNotificationCreated(wasCreated: Boolean?) {
        wasCreated?.let {
            if (wasCreated) {
                displaySnackbar("Created notification", Snackbar.LENGTH_SHORT)
                return
            }
        }
        displaySnackbar("Failed to create notification", Snackbar.LENGTH_SHORT)
    }

    private fun displaySnackbar(message: String, length: Int) = view?.let {
        Snackbar.make(it.rootView, message, length).show()
    }
}