package org.hackillinois.android.view.home.eventlist

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_schedule_day.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.view.eventinfo.EventInfoFragment
import org.hackillinois.android.view.schedule.EventsAdapter
import org.hackillinois.android.view.schedule.ScheduleListItem
import org.hackillinois.android.view.schedule.TimeListItem
import org.hackillinois.android.viewmodel.HomeViewModel

class EventsSectionFragment : Fragment(), EventClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: EventsAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var currentEvents: List<Event> = listOf()
    private var showFavorites: Boolean = false
    private var sectionNumber = 0

    private var listState: Parcelable? = null

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sectionNumber = arguments?.getInt(ARG_SECTION_NUM) ?: 0

        val viewModel = parentFragment?.let { ViewModelProviders.of(it).get(HomeViewModel::class.java) }

        val liveData = when (sectionNumber) {
            0 -> viewModel?.currentEventsLiveData
            1 -> viewModel?.upcomingEventsLiveData
//            2 -> viewModel?.asyncEventsLiveData
            else -> viewModel?.currentEventsLiveData
        }

        mAdapter = EventsAdapter(listOf(), this)

        liveData?.observe(
            this,
            Observer { events ->
                events?.let {
                    currentEvents = it
                    updateEvents(currentEvents)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_day, container, false)

        recyclerView = view.activity_schedule_recyclerview.apply {
            mLayoutManager = LinearLayoutManager(context)
            this.layoutManager = mLayoutManager
            this.adapter = mAdapter
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if (listState != null) {
            mLayoutManager.onRestoreInstanceState(listState)
        }
    }

    override fun onPause() {
        super.onPause()
        listState = mLayoutManager.onSaveInstanceState()
    }

    private fun updateEvents(list: List<Event>) {
        var listTemp = list
        context?.let {
            if (showFavorites) {
                listTemp = listTemp.filter { event -> FavoritesManager.isFavoritedEvent(it, event.id) }
            }
        }

        if (sectionNumber == 2) { // If async
            mAdapter.updateEvents(listTemp)
        } else {
            mAdapter.updateEvents(insertTimeItems(listTemp))
        }
    }

    private fun insertTimeItems(eventList: List<Event>): List<ScheduleListItem> {
        var currentTime = -1L
        val newList = mutableListOf<ScheduleListItem>()

        eventList.forEach {
            if (it.getStartTimeMs() != currentTime) {
                currentTime = it.getStartTimeMs()
                val timeListItem = TimeListItem(it.getStartTimeOfDay())
                newList.add(timeListItem)
            }
            newList.add(it)
        }

        return newList
    }

    companion object {
        private val ARG_SECTION_NUM = "section_number"

        fun newInstance(sectionNumber: Int): EventsSectionFragment {
            val fragment = EventsSectionFragment()
            val args = Bundle()

            args.putInt(ARG_SECTION_NUM, sectionNumber)
            fragment.arguments = args

            return fragment
        }
    }

    override fun openEventInfoActivity(event: Event) {
        val eventInfoFragment = EventInfoFragment.newInstance(event.id)
        (activity as MainActivity?)?.switchFragment(eventInfoFragment, true)
    }
}
