package org.hackillinois.android.view.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.TimeInfo
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.view.eventinfo.EventInfoFragment
import org.hackillinois.android.view.home.eventlist.EventClickListener
import org.hackillinois.android.view.home.eventlist.EventsSection
import org.hackillinois.android.view.home.eventlist.EventsSectionFragment
import org.hackillinois.android.viewmodel.HomeViewModel
import java.lang.Exception

class HomeFragment : Fragment(), CountdownManager.CountDownListener, EventClickListener {

    private lateinit var eventsListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var currentEventsSection: EventsSection
    private lateinit var upcomingEventsSection: EventsSection

    private lateinit var daysValue: TextView
    private lateinit var hoursValue: TextView
    private lateinit var minutesValue: TextView
    private lateinit var countdownTextView: TextView

    private lateinit var viewModel: HomeViewModel
    private val countDownManager = CountdownManager(this)

    private var isActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create EventsSections for current and upcoming events
        context?.let {
            val currentHeaderColor = Color.WHITE
            currentEventsSection = EventsSection(
                mutableListOf(),
                "Current",
                currentHeaderColor,
                false,
                this,
                it
            )
            val upcomingHeaderColor = getColor(context!!, R.color.primaryTextColor)
            upcomingEventsSection = EventsSection(
                mutableListOf(),
                "Upcoming",
                upcomingHeaderColor,
                false,
                this,
                it
            )
            eventsListAdapter = SectionedRecyclerViewAdapter().apply {
                addSection(currentEventsSection)
                addSection(upcomingEventsSection)
            }
        }

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.currentEventsLiveData.observe(this, Observer { updateCurrentEventsList(it) })
        viewModel.upcomingEventsLiveData.observe(this, Observer { updateUpcomingEventsList(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sectionsPagerAdapter = HomeSectionsPagerAdapter(childFragmentManager)

        view.homeScheduleContainer.adapter = sectionsPagerAdapter
        view.homeScheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.homeTabLayout))
        view.homeTabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.homeScheduleContainer))

        view.homeScheduleContainer.currentItem = 0

        daysValue = view.findViewById(R.id.daysValue)
        hoursValue = view.findViewById(R.id.hoursValue)
        minutesValue = view.findViewById(R.id.minutesValue)
        countdownTextView = view.findViewById(R.id.countdownTextView)

        return view
    }

    inner class HomeSectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = EventsSectionFragment.newInstance(position)
        override fun getCount() = 2
        override fun getPageTitle(position: Int) =
            when (position) {
                0 -> "Current"
                1 -> "Upcoming"
                else -> "Current"
            }
    }

    override fun onStart() {
        super.onStart()
        isActive = true
        countDownManager.start()
    }

    override fun onPause() {
        super.onPause()
        isActive = false
        countDownManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        isActive = true
        countDownManager.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val time = App.getAPI().times()
                countDownManager.setAPITimes(time)
            } catch (e: Exception) {
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    private fun updateCurrentEventsList(events: List<Event>?) {
        events?.let {
            currentEventsSection.updateEventsList(it)
            eventsListAdapter.notifyDataSetChanged()
        }
    }

    private fun updateUpcomingEventsList(events: List<Event>?) {
        events?.let {
            upcomingEventsSection.updateEventsList(events)
            eventsListAdapter.notifyDataSetChanged()
        }
    }

    private fun filterNextNUpcomingEvents(events: List<Event>, n: Int): List<Event> {
        val actualEvents = mutableListOf<Event>()
        var numLeft = n
        events.forEach {
            if (numLeft > 0) {
                actualEvents.add(it)
                numLeft--
            } else {
                if (actualEvents.isNotEmpty() && actualEvents.last().startTime == it.startTime) {
                    actualEvents.add(it)
                }
            }
        }
        return actualEvents
    }

    override fun updateTime(timeUntil: Long) {
        val timeInfo = TimeInfo(timeUntil)

        if (isActive) {
            daysValue.text = padNumber(timeInfo.days)
            hoursValue.text = padNumber(timeInfo.hours)
            minutesValue.text = padNumber(timeInfo.minutes)
        }
    }

    override fun updateTitle(newTitle: String) {
        if (isActive) {
            countdownTextView.text = newTitle
        }
    }

    override fun openEventInfoActivity(event: Event) {
        val eventInfoFragment = EventInfoFragment.newInstance(event.eventId)
        (activity as MainActivity?)?.switchFragment(eventInfoFragment, true)
    }

    private fun padNumber(number: Long) = String.format("%02d", number)
}
