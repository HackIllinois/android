package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

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
