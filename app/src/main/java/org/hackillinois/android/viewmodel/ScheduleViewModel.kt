package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.hackillinois.android.repository.EventRepository
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance

    val fridayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2020, Calendar.FEBRUARY, 29, 0, 0, 0)
    }.timeInMillis

    val saturdayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2020, Calendar.MARCH, 1, 0, 0, 0)
    }.timeInMillis

    val sundayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        set(2020, Calendar.MARCH, 2, 0, 0, 0)
    }.timeInMillis

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    fun init() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(0, fridayEnd)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayEnd, saturdayEnd)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(saturdayEnd, sundayEnd)
    }
}
