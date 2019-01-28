package org.hackillinois.android.view.admin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_admin_events.*
import kotlinx.android.synthetic.main.fragment_admin_events.view.*
import kotlinx.android.synthetic.main.fragment_admin_notifications.*
import kotlinx.android.synthetic.main.fragment_admin_notifications.view.*
import kotlinx.android.synthetic.main.fragment_admin_stats.*
import kotlinx.android.synthetic.main.fragment_admin_stats.view.*
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.model.EceBuilding
import org.hackillinois.android.model.Event
import org.hackillinois.android.model.EventLocation
import org.hackillinois.android.model.SiebelCenter
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.concurrent.thread

class ToolsFragment : Fragment() {
    private val ARG_SECTION_NUM = "section_number"

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
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View?

        val sectionNumber = if (arguments == null) 0 else arguments!!.getInt(ARG_SECTION_NUM)

        when (sectionNumber) {
            0 -> view = createStatsView(inflater, container, savedInstanceState)
            1 -> view = createEventsView(inflater, container, savedInstanceState)
            else -> view = createNotifcationsView(inflater, container, savedInstanceState)
        }

        return view
    }

    fun createStatsView(inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_stats, container, false)

        view.queryBtn.setOnClickListener {
            thread {
                val response = App.getAPI().stats.execute()
                response.body()?.let {
                    activity?.runOnUiThread {
                        val data = JSONObject(it.string()).toString(4)
                        statsText.text = data
                    }
                }
            }
        }

        return view
    }

    fun createEventsView(inflater: LayoutInflater, container: ViewGroup?,
                         savedInstanceState: Bundle?): View? {
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
                var location: EventLocation? = null
                when(locationName) {
                    "Siebel Center" -> location = EventLocation(SiebelCenter.description + " " + eventRoom, SiebelCenter.latitude, SiebelCenter.longitude)
                    "ECE Building" -> location = EventLocation(EceBuilding.description + " " + eventRoom, EceBuilding.latitude, EceBuilding.longitude)
                }

                location?.let {
                    val event = Event(name, description, startTime, endTime, listOf(location), sponsor, eventType)
                    App.getAPI().createEvent(event).enqueue(object: Callback<Event> {
                        override fun onFailure(call: Call<Event>, t: Throwable) {
                            Toast.makeText(activity?.applicationContext, "Failed to create event", Toast.LENGTH_SHORT).show()
                        }

                        override fun onResponse(call: Call<Event>, response: Response<Event>) {
                            if (response.code() == 200) {
                                Toast.makeText(activity?.applicationContext, "Created event", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(activity?.applicationContext, "Failed to create event", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            } catch (e: ParseException) {
                Toast.makeText(activity?.applicationContext, "Failed to parse date time", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun createNotifcationsView(inflater: LayoutInflater, container: ViewGroup?,
                         savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_notifications, container, false)

        view.notificationCreateBtn.setOnClickListener {
            // Create notification
        }

        return view
    }
}