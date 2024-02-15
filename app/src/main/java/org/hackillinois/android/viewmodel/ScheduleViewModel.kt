package org.hackillinois.android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    lateinit var fridayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var saturdayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>
    lateinit var sundayEventsLiveData: LiveData<List<org.hackillinois.android.database.entity.Event>>

    lateinit var fridayShiftsLiveData: LiveData<List<org.hackillinois.android.database.entity.Shift>>
    lateinit var saturdayShiftsLiveData: LiveData<List<org.hackillinois.android.database.entity.Shift>>
    lateinit var sundayShiftsLiveData: LiveData<List<org.hackillinois.android.database.entity.Shift>>

    var showFavorites: MutableLiveData<Boolean> = MutableLiveData()
    var showShifts: MutableLiveData<Boolean> = MutableLiveData()
    var isAttendeeViewing: Boolean = true

    fun initEvents() {
        fridayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayStart, fridayEnd)
        saturdayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(fridayEnd, saturdayEnd)
        sundayEventsLiveData = eventRepository.fetchEventsHappeningBetweenTimes(saturdayEnd, sundayEnd)
        viewModelScope.launch {
            eventRepository.refreshAllEvents()
        }
    }

    fun initShifts() {
        fridayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(fridayStart, fridayEnd)
        saturdayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(fridayEnd, saturdayEnd)
        sundayShiftsLiveData = shiftRepository.fetchShiftsHappeningBetweenTimes(saturdayEnd, sundayEnd)
        viewModelScope.launch {
            shiftRepository.refreshAllShifts()
        }
    }
}
