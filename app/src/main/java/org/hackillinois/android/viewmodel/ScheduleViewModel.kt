package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.repository.EventRepository
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    val fridayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2021, Calendar.APRIL, 9, 0, 0, 0)
    }.timeInMillis

    val saturdayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2021, Calendar.APRIL, 10, 0, 0, 0)
    }.timeInMillis

    val sundayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2021, Calendar.APRIL, 11, 0, 0, 0)
    }.timeInMillis

    val mondayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2021, Calendar.APRIL, 12, 0, 0, 0)
    }.timeInMillis

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var mondayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    var showFavorites: MutableLiveData<Boolean> = MutableLiveData()

    fun init() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(0, fridayEnd)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayEnd, saturdayEnd)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(saturdayEnd, sundayEnd)
        mondayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(sundayEnd, mondayEnd)
        viewModelScope.launch {
            eventRepository.refreshAllEvents()
        }
    }
}
