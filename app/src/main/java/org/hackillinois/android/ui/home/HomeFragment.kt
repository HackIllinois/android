package org.hackillinois.android.ui.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.hackillinois.android.R
import org.hackillinois.android.model.EventsList
import org.hackillinois.android.utils.StartTimes
import org.hackillinois.android.utils.TimeInfo
import org.hackillinois.android.viewmodels.home.HomeViewModel
import java.util.*

class HomeFragment : Fragment(), CountdownManager.CountDownListener {

    private val eventsAdapter = EventsListAdapter(mutableListOf())

    private val eventTimes = listOf(StartTimes.eventStartTime, StartTimes.hackingStartTime, StartTimes.hackingEndTime)
    private val titles = listOf("EVENT STARTS IN", "HACKING STARTS IN", "HACKING ENDS IN", "THANKS FOR COMING!")

    private lateinit var viewModel: HomeViewModel
    private val countDownManager = CountdownManager(this, eventTimes)

    private var title = "Title"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.eventsListLiveData.observe(this, Observer { updateEventsList(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.fetchEvents()
        countDownManager.start()

        view.eventsList.apply {
            layoutManager = LinearLayoutManager(container?.context)
            adapter = eventsAdapter
        }

        view.titleMessage.text = title

        return view
    }

    override fun onPause() {
        super.onPause()
        countDownManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        countDownManager.onResume()
    }

    private fun updateEventsList(eventsList: EventsList?) {
        eventsList?.let {
            eventsAdapter.updateEventsList(it.events)
        }
    }

    override fun updateTime(timeInfo: TimeInfo) {
        if (isVisible) {
            previousDayTextView.text = padNumber(timeInfo.days + 1)
            nextDayTextView.text = padNumber(timeInfo.days - 1)

            previousHourTextView.text = padNumber((timeInfo.hours + 24 + 1) % 24)
            nextHourTextView.text = padNumber((timeInfo.hours + 24 - 1) % 24)

            previousMinuteTextView.text = padNumber((timeInfo.minutes + 60 + 1) % 60)
            nextMinuteTextView.text = padNumber((timeInfo.minutes + 60 - 1) % 60)

            previousSecondTextView.text = padNumber((timeInfo.seconds + 60 + 1) % 60)
            nextSecondTextView.text = padNumber((timeInfo.seconds + 60 - 1) % 60)

            if (timeInfo.days == 0L) {
                dayLayout.visibility = View.GONE
                dayLabel.visibility = View.GONE
            }

            currentDayTextView.text = padNumber(timeInfo.days)
            currentHourTextView.text = padNumber(timeInfo.hours)
            currentMinuteTextView.text = padNumber(timeInfo.minutes)
            currentSecondTextVIew.text = padNumber(timeInfo.seconds)
        }
    }

    override fun updateTimer(index: Int) {
        title = titles[index]
        if (isVisible) {
            titleMessage.text = titles[index]
        }
    }

    private fun padNumber(number: Long): String {
        val temp = number.toString()
        return when (temp.length) {
            0 -> "00"
            1 -> "0$temp"
            else -> temp
        }
    }
}
