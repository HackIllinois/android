package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.hackillinois.android.repository.EventRepository
import java.util.TimeZone
import java.util.Calendar

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    private val FRIDAY_END = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 23, 0, 0, 0)
    }.timeInMillis
    private val SATURDAY_END = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 24, 0, 0, 0)
    }.timeInMillis
    private val SUNDAY_END = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2019, Calendar.FEBRUARY, 25, 0, 0, 0)
    }.timeInMillis

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    fun init() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(0, FRIDAY_END)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(FRIDAY_END, SATURDAY_END)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(SATURDAY_END, SUNDAY_END)
    }
}
