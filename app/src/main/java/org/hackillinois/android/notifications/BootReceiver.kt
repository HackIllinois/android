package org.hackillinois.android.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.hackillinois.android.App
import org.hackillinois.android.common.FavoritesManager
import kotlin.concurrent.thread

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        thread {
            val events = App.database.eventDao().getAllEventsList()
            events.filter {
                FavoritesManager.isFavorited(context, it.name)
            }.forEach {
                HackIllinoisNotificationManager.scheduleEventNotification(context, it)
            }
        }
    }
}
