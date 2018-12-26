package org.hackillinois.android.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import org.hackillinois.android.model.Event
import org.hackillinois.android.common.FavoritesManager
import org.hackillinois.android.common.HackIllinoisNotificationManager

class EventInfoViewModel(val app: Application): AndroidViewModel(app) {
    val isFavorited = MutableLiveData<Boolean>()
    var localIsFavorited = false

    fun getIsFavorited(event: Event) {
        localIsFavorited = FavoritesManager.isFavorited(app.applicationContext, event)
        isFavorited.postValue(localIsFavorited)
    }

    fun changeFavoritedState(event: Event) {
        if (localIsFavorited) {
            FavoritesManager.unfavoriteEvent(app.applicationContext, event)
            HackIllinoisNotificationManager.cancelNotification(app.applicationContext, event)
        } else {
            FavoritesManager.favoriteEvent(app.applicationContext, event)
            HackIllinoisNotificationManager.scheduleNotification(app.applicationContext, event)
        }
        localIsFavorited = !localIsFavorited
        isFavorited.postValue(localIsFavorited)
    }
}
