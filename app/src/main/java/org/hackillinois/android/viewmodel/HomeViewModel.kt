package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    lateinit var ongoingEventsLiveData: LiveData<List<Event>>
    lateinit var upcomingEventsLiveData: LiveData<List<Event>>

    private val currentTime: MutableLiveData<Long> = MutableLiveData()

    fun init() {
        ongoingEventsLiveData = Transformations.switchMap(currentTime) {
            value -> eventRepository.fetchEventsHappeningAtTime(value)
        }
        upcomingEventsLiveData = Transformations.switchMap(currentTime) {
            value -> eventRepository.fetchEventsAfter(value)
        }
        refresh()
    }

    fun refresh() {
        currentTime.value = System.currentTimeMillis()
    }
}
