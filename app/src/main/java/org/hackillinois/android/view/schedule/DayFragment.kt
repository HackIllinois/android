package org.hackillinois.android.view.schedule

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_schedule_day.view.*
import org.hackillinois.android.R
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Shift
import org.hackillinois.android.view.MainActivity
import org.hackillinois.android.viewmodel.ScheduleViewModel

class DayFragment : Fragment(), EventClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: EventsAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    private var currentEvents: List<Event> = listOf()
    private var currentShifts: List<Shift> = listOf()
    private var showFavorites: Boolean = false
    private var showShifts: Boolean = false
    private var isAttendeeViewing: Boolean = false

    private var listState: Parcelable? = null

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sectionNumber = arguments?.getInt(ARG_SECTION_NUM) ?: 0

        val viewModel = parentFragment?.let { ViewModelProvider(it).get(ScheduleViewModel::class.java) }
        viewModel?.init()

        val liveEventData = when (sectionNumber) {
            0 -> viewModel?.fridayEventsLiveData
            1 -> viewModel?.saturdayEventsLiveData
            2 -> viewModel?.sundayEventsLiveData
            else -> viewModel?.fridayEventsLiveData
        }

        val liveShiftData = when (sectionNumber) {
            0 -> viewModel?.fridayShiftsLiveData
            1 -> viewModel?.saturdayShiftsLiveData
            2 -> viewModel?.sundayShiftsLiveData
            else -> viewModel?.fridayShiftsLiveData
        }

        isAttendeeViewing = viewModel?.isAttendeeViewing ?: true
        mAdapter = EventsAdapter(listOf(), this, isAttendeeViewing)

        viewModel?.showShifts?.observe(
            this,
            Observer {
                showShifts = it
                if (showShifts) {
                    Log.d("Observe showShifts", "Switching to SHIFTS")
                    mAdapter.updateEvents(insertTimeItems(currentShifts))
                } else {
                    Log.d("Observe showShifts", "Switching to SCHEDULE")
                    updateEvents(currentEvents)
                }
            }
        )

        liveEventData?.observe(
            this,
            Observer { events ->
                events?.let {
                    currentEvents = it
                    if (isAttendeeViewing || !showShifts) {
                        updateEvents(currentEvents)
                    }
                }
            }
        )

        liveShiftData?.observe(
            this,
            Observer { shifts ->
                shifts?.let {
                    currentShifts = it
                    if (showShifts) {
                        mAdapter.updateEvents(insertTimeItems(currentShifts))
                    }
                }
            }
        )

        if (isAttendeeViewing) {
            viewModel?.showFavorites?.observe(
                this,
                Observer {
                    showFavorites = it
                    updateEvents(currentEvents)
                }
            )
        }
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

    override fun openEventInfoActivity(event: Event) {
        val eventInfoFragment = EventInfoFragment.newInstance(event.eventId, isAttendeeViewing)
        (activity as MainActivity?)?.switchFragment(eventInfoFragment, true)
    }

    private fun updateEvents(list: List<Event>) {
        var listTemp = list
        context?.let {
            if (showFavorites) {
                listTemp = listTemp.filter { event -> FavoritesManager.isFavoritedEvent(it, event.eventId) }
            }
        }
        mAdapter.updateEvents(insertTimeItems(listTemp))
    }

    private fun insertTimeItems(eventList: List<ScheduleListItem>): List<ScheduleListItem> {
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

        fun newInstance(sectionNumber: Int): DayFragment {
            val fragment = DayFragment()
            val args = Bundle()

            args.putInt(ARG_SECTION_NUM, sectionNumber)
            fragment.arguments = args

            return fragment
        }
    }
}
