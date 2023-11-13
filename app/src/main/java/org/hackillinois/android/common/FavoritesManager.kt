package org.hackillinois.android.common

import android.content.Context
import android.util.Log
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.notifications.HackIllinoisNotificationManager
import org.hackillinois.android.notifications.HackIllinoisNotificationManager.cancelEventNotification
import org.hackillinois.android.notifications.HackIllinoisNotificationManager.scheduleEventNotification

class FavoritesManager {
    companion object {
        fun favoriteEvent(context: Context, event: Event?) = event?.let {
            setBoolean(context, it.eventId, true)
            HackIllinoisNotificationManager.scheduleEventNotification(context, it)
        }

        fun unfavoriteEvent(context: Context, event: Event?) = event?.let {
            setBoolean(context, it.eventId, false)
            HackIllinoisNotificationManager.cancelEventNotification(context, it)
        }

        fun isFavoritedEvent(context: Context, eventId: String) = getBoolean(context, eventId)

        fun clearFavorites(context: Context) {
            getFavoritesPrefs(context).edit().clear().apply()
        }

        private fun setBoolean(context: Context, key: String, value: Boolean) {
            getFavoritesPrefs(context).edit().apply {
                putBoolean(key, value)
                apply()
            }
        }

        private fun getBoolean(context: Context, key: String) = getFavoritesPrefs(context).getBoolean(key, false)
        private fun getFavoritesPrefs(context: Context) = context.getSharedPreferences(context.getString(R.string.favorites_pref_file_key), Context.MODE_PRIVATE)

        fun updateFavoriteNotifications(context: Context, oldEvents: List<Event>, newEvents: List<Event>) {
            newEvents.forEach { newEvent ->
                if (isFavoritedEvent(context, newEvent.eventId)) {
                    var oldEvent = oldEvents.find { it.eventId == newEvent.eventId }
                    oldEvent?.let {
                        if (oldEvent.startTime != newEvent.startTime) {
                            cancelEventNotification(context, oldEvent)
                            scheduleEventNotification(context, newEvent)
                            Log.d("TAG", "Event Updated :) " + newEvent.name)
                        }
                    }
                }
            }
        }
    }
}
