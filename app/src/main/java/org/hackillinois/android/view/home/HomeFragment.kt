package org.hackillinois.android.view.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.hackillinois.android.R
import org.hackillinois.android.custom.CustomRefreshView
import org.hackillinois.android.model.Event
import org.hackillinois.android.utils.TimeInfo
import org.hackillinois.android.viewmodel.HomeViewModel

class HomeFragment : Fragment(), CountdownManager.CountDownListener {

    private val eventsAdapter = EventsListAdapter(mutableListOf())

    private lateinit var viewModel: HomeViewModel
    private val countDownManager = CountdownManager(this)

    private val SECONDS_IN_MINUTE = 60
    private val MINUTES_IN_HOUR = 60
    private val HOURS_IN_DAY = 24

    private var isActive = false

    private val refreshIconSize = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.eventsListLiveData.observe(this, Observer { updateEventsList(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.fetchEvents()

        view.eventsList.apply {
            layoutManager = LinearLayoutManager(container?.context)
            adapter = eventsAdapter
        }

        view.refreshLayout.setRefreshView(CustomRefreshView(context), ViewGroup.LayoutParams(refreshIconSize, refreshIconSize))
        view.refreshLayout.setOnRefreshListener {
            viewModel.fetchEvents()
        }

        return view
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
    }

    override fun onStop() {
        super.onStop()
        isActive = false
    }

    private fun updateEventsList(events: List<Event>?) {
        events?.let {
            eventsAdapter.updateEventsList(it)
        }
        refreshLayout.setRefreshing(false)
    }

    override fun updateTime(timeUntil: Long) {
        val timeInfo = TimeInfo(timeUntil)

        if (isActive) {
            if (timeInfo.days > 0L) {
                dayLayout.visibility = View.VISIBLE
                dayLabel.visibility = View.VISIBLE

                previousDayTextView.text = padNumber(timeInfo.days + 1)
                currentDayTextView.text = padNumber(timeInfo.days)
                nextDayTextView.text = padNumber(timeInfo.days - 1)
            } else {
                dayLayout.visibility = View.GONE
                dayLabel.visibility = View.GONE
            }

            previousHourTextView.text = padNumber(modularAdd(timeInfo.hours, 1, HOURS_IN_DAY))
            currentHourTextView.text = padNumber(timeInfo.hours)
            nextHourTextView.text = padNumber(modularAdd(timeInfo.hours, -1, HOURS_IN_DAY))

            previousMinuteTextView.text = padNumber(modularAdd(timeInfo.minutes, 1, MINUTES_IN_HOUR))
            currentMinuteTextView.text = padNumber(timeInfo.minutes)
            nextMinuteTextView.text = padNumber(modularAdd(timeInfo.minutes, -1, MINUTES_IN_HOUR))

            previousSecondTextView.text = padNumber(modularAdd(timeInfo.seconds, 1, SECONDS_IN_MINUTE))
            currentSecondTextVIew.text = padNumber(timeInfo.seconds)
            nextSecondTextView.text = padNumber(modularAdd(timeInfo.seconds, -1, SECONDS_IN_MINUTE))
        }
    }

    override fun updateTitle(newTitle: String) {
        if (isActive) {
            titleMessage.text = newTitle
        }
    }

    private fun padNumber(number: Long) = String.format("%02d", number)
    private fun modularAdd(number: Long, add: Int, mod: Int) = (number + add + mod) % mod
}
