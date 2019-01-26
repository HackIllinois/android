package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    lateinit var eventsListLiveData: LiveData<List<Event>>

    fun init() {
        eventsListLiveData = eventRepository.fetchEventsHappeningAtTime(System.currentTimeMillis())
    }

    fun refresh() {
        eventsListLiveData = eventRepository.fetchEventsHappeningAtTime(System.currentTimeMillis())
        eventRepository.forceRefreshAll()
    }
}
