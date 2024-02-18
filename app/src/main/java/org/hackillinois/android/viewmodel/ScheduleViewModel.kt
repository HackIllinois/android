package org.hackillinois.android.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.ShiftRepository
import java.util.*

class ScheduleViewModel : ViewModel() {
    private val eventRepository = EventRepository.instance
    private val shiftRepository = ShiftRepository.instance

    // 2/23/24 00:00:00
    val fridayStart = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708668000000
    }.timeInMillis

    // 2/23/24 23:59:59
    val fridayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708754399000
    }.timeInMillis

    // 2/24/24 23:59:59
    val saturdayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708840799000
    }.timeInMillis

    // 2/25/24 23:59:59
    val sundayEnd = Calendar.getInstance().apply {
        timeZone = TimeZone.getTimeZone("America/Chicago")
        timeInMillis = 1708927199000
    }.timeInMillis

    var fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayStart, fridayEnd)
    var saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayEnd, saturdayEnd)
    var sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(saturdayEnd, sundayEnd)

    var fridayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(fridayStart, fridayEnd)
    var saturdayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(fridayEnd, saturdayEnd)
    var sundayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(saturdayEnd, sundayEnd)

    var showFavorites: MutableLiveData<Boolean> = MutableLiveData()
    var showShifts: MutableLiveData<Boolean> = MutableLiveData()
    var isAttendeeViewing: Boolean = true
    val isLoaded: MutableLiveData<Boolean> = MutableLiveData()

    fun initEvents() {
        GlobalScope.launch {
            eventRepository.refreshAllEvents()
        }
    }

    fun initShifts() {
        GlobalScope.launch {
            shiftRepository.refreshAllShifts()
        }
    }
}
