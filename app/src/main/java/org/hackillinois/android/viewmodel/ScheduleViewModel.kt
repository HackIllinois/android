package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.repository.EventRepository


class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance
    lateinit var eventsListLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    fun init() {
        eventsListLiveData = eventRepository.fetchAllEvents();
    }
}
