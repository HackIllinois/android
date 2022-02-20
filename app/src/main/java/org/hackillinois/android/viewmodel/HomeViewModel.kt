package org.hackillinois.android.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.repository.EventRepository

class HomeViewModel : ViewModel() {

    private val eventRepository = EventRepository.instance
    var ongoingEventsLiveData: LiveData<List<Event>>
    var upcomingEventsLiveData: LiveData<List<Event>>
    var asyncEventsLiveData: LiveData<List<Event>>

    private val NEXT_TWO_HOURS_MS = 1000 * 60 * 120

    private val currentTime: MutableLiveData<Long> = MutableLiveData()

    init {
        ongoingEventsLiveData = Transformations.switchMap(currentTime) { value ->
            eventRepository.fetchEventsHappeningAtTime(value)
        }
        upcomingEventsLiveData = Transformations.switchMap(currentTime) { value ->
            eventRepository.fetchEventsHappeningBetweenTimes(value, value + NEXT_TWO_HOURS_MS)
        }
        asyncEventsLiveData = eventRepository.fetchAsyncEvents()
        refresh()
    }

    fun refresh() {
        currentTime.value = System.currentTimeMillis()
        viewModelScope.launch {
            eventRepository.refreshAllEvents()
        }
    }
}
