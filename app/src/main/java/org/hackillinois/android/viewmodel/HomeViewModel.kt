package org.hackillinois.android.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    var ongoingEventsLiveData: LiveData<List<Event>>
    var upcomingEventsLiveData: LiveData<List<Event>>

    private val currentTime: MutableLiveData<Long> = MutableLiveData()

    init {
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
        viewModelScope.launch {
            eventRepository.refreshAllEvents()
        }
    }
}
