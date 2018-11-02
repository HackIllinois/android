package org.hackillinois.android.ui.home

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.hackillinois.android.R
import org.hackillinois.android.model.EventsList
import org.hackillinois.android.viewmodels.home.HomeViewModel

class HomeFragment : Fragment() {

    private val eventsAdapter = EventsListAdapter(mutableListOf())

    private lateinit var viewModel: HomeViewModel

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

        return view
    }

    private fun updateEventsList(eventsList: EventsList?) {
        eventsList?.let {
            eventsAdapter.updateEventsList(it.events)
        }
    }
}
