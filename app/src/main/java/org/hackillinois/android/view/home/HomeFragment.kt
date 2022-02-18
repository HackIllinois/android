package org.hackillinois.android.view.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R
import org.hackillinois.android.common.TimeInfo
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.view.custom.CustomRefreshView
import org.hackillinois.android.view.eventinfo.EventInfoFragment
import org.hackillinois.android.view.home.eventlist.EventClickListener
import org.hackillinois.android.view.home.eventlist.EventsSection
import org.hackillinois.android.view.home.eventlist.EventsSectionFragment
import org.hackillinois.android.view.schedule.DayFragment
import org.hackillinois.android.viewmodel.HomeViewModel
import java.lang.Exception

class HomeFragment : Fragment(), CountdownManager.CountDownListener, EventClickListener {

    private lateinit var eventsListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private lateinit var ongoingEventsSection: EventsSection
    private lateinit var upcomingEventsSection: EventsSection
    private lateinit var asyncEventsSection: EventsSection

    private lateinit var viewModel: HomeViewModel
    private val countDownManager = CountdownManager(this)

    private var isActive = false

    private val refreshIconSize = 100

    private val numberOfUpcomingEvents = 2
    private lateinit var layout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context?.let {

            val ongoingHeaderColor = Color.WHITE
            ongoingEventsSection = EventsSection(
                    mutableListOf(),
                    "Ongoing",
                    ongoingHeaderColor,
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
            val asyncHeaderColor = Color.WHITE
            asyncEventsSection = EventsSection(
                    mutableListOf(),
                    "Async",
                    asyncHeaderColor,
                    false,
                    this,
                    it
            )
            eventsListAdapter = SectionedRecyclerViewAdapter().apply {
                addSection(ongoingEventsSection)
                addSection(upcomingEventsSection)
                addSection(asyncEventsSection)

            }
        }

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.ongoingEventsLiveData.observe(this, Observer { updateOngoingEventsList(it) })
        viewModel.upcomingEventsLiveData.observe(this, Observer { updateUpcomingEventsList(it) })
        viewModel.asyncEventsLiveData.observe(this, Observer { updateAsyncEventsList(it) })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val sectionsPagerAdapter = HomeSectionsPagerAdapter(childFragmentManager)

        view.homeScheduleContainer.adapter = sectionsPagerAdapter
        view.homeScheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.homeTabLayout))
        view.homeTabLayout.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.homeScheduleContainer))

        view.homeScheduleContainer.currentItem = 0

        return view
    }

    inner class HomeSectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = EventsSectionFragment.newInstance(position)
        override fun getCount() = 3
        override fun getPageTitle(position: Int) =
                when (position) {
                    0 -> "Ongoing"
                    1 -> "Upcoming"
                    2 -> "Async"
                    else -> "Ongoing"
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
            } catch (e: Exception) {}
        }
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    private fun updateOngoingEventsList(events: List<Event>?) {
        events?.let {
            ongoingEventsSection.updateEventsList(it)
            eventsListAdapter.notifyDataSetChanged()
        }
    }

    private fun updateUpcomingEventsList(events: List<Event>?) {
        events?.let {
            val actualEvents = filterNextNUpcomingEvents(it, numberOfUpcomingEvents)
            upcomingEventsSection.updateEventsList(actualEvents)
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

    private fun updateAsyncEventsList(events: List<Event>?) {
        events?.let {
            val actualEvents = filterNextNUpcomingEvents(it, numberOfUpcomingEvents)
            asyncEventsSection.updateEventsList(actualEvents)
            eventsListAdapter.notifyDataSetChanged()
        }
    }

    override fun updateTime(timeUntil: Long) {
        val timeInfo = TimeInfo(timeUntil)

        if (isActive) {
            daysValue.setText(padNumber(timeInfo.days))
            hoursValue.setText(padNumber(timeInfo.hours))
            minutesValue.setText(padNumber(timeInfo.minutes))
        }
    }

    override fun updateTitle(newTitle: String) {
        if (isActive) {
            countdownTextView.text = newTitle
        }
    }

    override fun openEventInfoActivity(event: Event) {
        val eventInfoFragment = EventInfoFragment.newInstance(event.id)
        (activity as MainActivity?)?.switchFragment(eventInfoFragment, true)
    }

    private fun padNumber(number: Long) = String.format("%02d", number)
}
