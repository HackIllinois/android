package org.hackillinois.android.view.schedule

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_schedule_day.view.*

import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.viewmodel.ScheduleViewModel

class DayFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<*>? = null
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var sortedEvents: List<Event>? = null
    private var listState: Parcelable? = null

    override fun onResume() {
        super.onResume()
        adapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sectionNumber = arguments?.getInt(ARG_SECTION_NUM) ?: 0

        val viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        viewModel.init()

        val liveData = when (sectionNumber) {
            0 -> viewModel.fridayEventsLiveData
            1 -> viewModel.saturdayEventsLiveData
            else -> viewModel.sundayEventsLiveData
        }

        liveData.observe(this, Observer { events ->
            events?.let {
                sortedEvents = events
                adapter = EventsAdapter(events)
                recyclerView.adapter = adapter
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule_day, container, false)

        recyclerView = view.activity_schedule_recyclerview
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        sortedEvents?.let {
            adapter = EventsAdapter(it)
            recyclerView.adapter = adapter
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState)
        }
    }

    override fun onPause() {
        super.onPause()
        listState = layoutManager.onSaveInstanceState()
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