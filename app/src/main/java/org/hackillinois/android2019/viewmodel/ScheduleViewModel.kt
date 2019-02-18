package org.hackillinois.android2019.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android2019.repository.EventRepository
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    val fridayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 23, 0, 0, 0)
    }.timeInMillis

    val saturdayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 24, 0, 0, 0)
    }.timeInMillis

    val sundayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 25, 0, 0, 0)
    }.timeInMillis

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android2019.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android2019.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android2019.database.entity.Event>>

    fun init() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(0, fridayEnd)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayEnd, saturdayEnd)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(saturdayEnd, sundayEnd)
    }

    fun refresh() {
        eventRepository.refreshAll()
    }
}
