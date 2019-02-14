package org.hackillinois.android2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android2019.database.entity.Event
import org.hackillinois.android2019.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    lateinit var eventsListLiveData: LiveData<List<Event>>

    fun init() {
        eventsListLiveData = eventRepository.fetchEventsHappeningAtTime(System.currentTimeMillis())
    }

    fun refresh() {
        eventsListLiveData = eventRepository.forceFetchEventsHappeningAtTime(System.currentTimeMillis())
    }
}
