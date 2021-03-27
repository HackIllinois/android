package org.hackillinois.android.common

import android.content.Context
import android.util.Log
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import org.hackillinois.android.database.entity.Project
import org.hackillinois.android.notifications.HackIllinoisNotificationManager
import org.hackillinois.android.notifications.HackIllinoisNotificationManager.cancelEventNotification
import org.hackillinois.android.notifications.HackIllinoisNotificationManager.scheduleEventNotification

class FavoritesManager {
    companion object {
        fun favoriteEvent(context: Context, event: Event?) = event?.let {
            setBoolean(context, it.id, true)
            HackIllinoisNotificationManager.scheduleEventNotification(context, it)
        }

        fun favoriteProject(context: Context, project: Project?) = project?.let {
            setBoolean(context, it.id, true)
        }

        fun unfavoriteEvent(context: Context, event: Event?) = event?.let {
            setBoolean(context, it.id, false)
            HackIllinoisNotificationManager.cancelEventNotification(context, it)
        }

        fun unfavoriteProject(context: Context, project: Project?) = project?.let {
            setBoolean(context, it.id, false)
        }

        fun isFavoritedEvent(context: Context, eventId: String) = getBoolean(context, eventId)

        fun isFavoritedProject(context: Context, projectId: String) = getBoolean(context, projectId)

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
            newEvents.forEach {
                newEvent ->
                if (isFavoritedEvent(context, newEvent.id)) {
                    var oldEvent = oldEvents.find { it.id == newEvent.id }
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