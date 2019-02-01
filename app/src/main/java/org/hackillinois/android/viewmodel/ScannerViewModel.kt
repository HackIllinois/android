package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class ScannerViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    lateinit var eventsListLiveData: LiveData<List<Event>>

    fun init() {
        eventsListLiveData = eventRepository.fetchAllEvents()
    }
}
