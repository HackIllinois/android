package org.hackillinois.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.HackIllinoisNotificationManager
import org.hackillinois.android.repository.EventRepository

class EventInfoViewModel(val app: Application): AndroidViewModel(app) {

    private val eventRepository = EventRepository.instance
    lateinit var event: LiveData<Event>

    val isFavorited = MutableLiveData<Boolean>()
    var localIsFavorited = false

    fun init(name: String) {
        event = eventRepository.fetchEvent(name)
    }

    fun getIsFavorited(eventName: String) {
        localIsFavorited = FavoritesManager.isFavorited(app.applicationContext, eventName)
        isFavorited.postValue(localIsFavorited)
    }

    fun changeFavoritedState() {
        if (localIsFavorited) {
            FavoritesManager.unfavoriteEvent(app.applicationContext, event.value?.name)
            HackIllinoisNotificationManager.cancelNotification(app.applicationContext, event.value)
        } else {
            FavoritesManager.favoriteEvent(app.applicationContext, event.value?.name)
            HackIllinoisNotificationManager.scheduleNotification(app.applicationContext, event.value)
        }
        localIsFavorited = !localIsFavorited
        isFavorited.postValue(localIsFavorited)
    }
}
