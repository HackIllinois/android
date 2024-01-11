package org.hackillinois.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Roles
import org.hackillinois.android.repository.EventRepository
import org.hackillinois.android.repository.rolesRepository

class EventInfoViewModel(val app: Application) : AndroidViewModel(app) {

    private val eventRepository = EventRepository.instance
    lateinit var event: LiveData<Event>

    lateinit var roles: LiveData<Roles>

    val isFavorited = MutableLiveData<Boolean>()

    fun init(id: String) {
        viewModelScope.launch {
            event = eventRepository.fetchEvent(id)
            roles = rolesRepository.fetch()

            val favorited = FavoritesManager.isFavoritedEvent(app.applicationContext, id)
            isFavorited.postValue(favorited)
        }
    }

    fun changeFavoritedState(): Boolean {
        var favorited = isFavorited.value ?: false
        favorited = !favorited

        isFavorited.postValue(favorited)

        if (favorited) {
            FavoritesManager.favoriteEvent(app.applicationContext, event.value)
        } else {
            FavoritesManager.unfavoriteEvent(app.applicationContext, event.value)
        }
        return favorited
    }
}
