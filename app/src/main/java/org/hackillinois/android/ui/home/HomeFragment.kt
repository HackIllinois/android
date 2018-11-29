package org.hackillinois.android.ui.home

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
import org.hackillinois.android.model.EventsList
import org.hackillinois.android.utils.TimeInfo
import org.hackillinois.android.viewmodels.home.HomeViewModel
import java.util.*

class HomeFragment : Fragment(), CountdownManager.CountDownListener {

    private val eventsAdapter = EventsListAdapter(mutableListOf())

    private lateinit var viewModel: HomeViewModel
    private lateinit var countDownManager: CountdownManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.eventsListLiveData.observe(this, Observer { updateEventsList(it) })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.fetchEvents()

        val time = Calendar.getInstance().apply {
            set(2018, Calendar.NOVEMBER, 28, 20, 0, 0)
        }

        countDownManager = CountdownManager(this, time)
        countDownManager.startTimer()

        view.eventsList.apply {
            layoutManager = LinearLayoutManager(container?.context)
            adapter = eventsAdapter
        }

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
            previousHourTextView.text = padNumber((timeInfo.hours + 24 + 1) % 24)
            nextHourTextView.text = padNumber((timeInfo.hours + 24 - 1) % 24)

            previousMinuteTextView.text = padNumber((timeInfo.minutes + 60 + 1) % 60)
            nextMinuteTextView.text = padNumber((timeInfo.minutes + 60 - 1) % 60)

            previousSecondTextView.text = padNumber((timeInfo.seconds + 60 + 1) % 60)
            nextSecondTextView.text = padNumber((timeInfo.seconds + 60 - 1) % 60)

            currentHourTextView.text = padNumber(timeInfo.hours)
            currentMinuteTextView.text = padNumber(timeInfo.minutes)
            currentSecondTextVIew.text = padNumber(timeInfo.seconds)
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
