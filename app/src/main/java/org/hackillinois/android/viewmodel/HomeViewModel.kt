package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    lateinit var ongoingEventsLiveData: LiveData<List<Event>>
    lateinit var upcomingEventsLiveData: LiveData<List<Event>>

    fun init() {
        ongoingEventsLiveData = eventRepository.fetchEventsHappeningAtTime(System.currentTimeMillis())
        upcomingEventsLiveData = eventRepository.fetchEventsHappeningInNextHour()
    }

    fun refresh() {
        ongoingEventsLiveData = eventRepository.forceFetchEventsHappeningAtTime(System.currentTimeMillis())
        upcomingEventsLiveData = eventRepository.fetchEventsHappeningInNextHour()
    }
}
