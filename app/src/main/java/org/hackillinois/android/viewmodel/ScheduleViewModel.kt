package org.hackillinois.android.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import java.sql.Timestamp
import org.hackillinois.android.repository.EventRepository

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    val FRIDAY_END = Timestamp.valueOf("2019-02-23 00:00:00").time
    val SATURDAY_END = Timestamp.valueOf("2019-02-24 00:00:00").time
    val SUNDAY_END = Timestamp.valueOf("2019-02-25 00:00:00").time

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    fun init() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(0, FRIDAY_END)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(FRIDAY_END, SATURDAY_END)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(SATURDAY_END, SUNDAY_END)
    }
}
