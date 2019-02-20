package org.hackillinois.androidapp2019.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import org.hackillinois.androidapp2019.common.FavoritesManager
import org.hackillinois.androidapp2019.database.entity.Event
import org.hackillinois.androidapp2019.repository.EventRepository

class EventInfoViewModel(val app: Application) : AndroidViewModel(app) {

    private val eventRepository = EventRepository.instance
    lateinit var event: LiveData<Event>

    val isFavorited = MutableLiveData<Boolean>()

    fun init(name: String) {
        event = eventRepository.fetchEvent(name)

        val favorited = FavoritesManager.isFavorited(app.applicationContext, name)
        isFavorited.postValue(favorited)
    }

    fun changeFavoritedState() {
        var favorited = isFavorited.value ?: false
        favorited = !favorited

        isFavorited.postValue(favorited)

        if (favorited) {
            FavoritesManager.favoriteEvent(app.applicationContext, event.value)
        } else {
            FavoritesManager.unfavoriteEvent(app.applicationContext, event.value)
        }
    }
}
