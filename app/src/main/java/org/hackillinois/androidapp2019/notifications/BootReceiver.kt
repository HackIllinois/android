package org.hackillinois.androidapp2019.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.hackillinois.androidapp2019.App
import org.hackillinois.androidapp2019.common.FavoritesManager
import kotlin.concurrent.thread

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        thread {
            val events = App.getDatabase().eventDao().getAllEventsList()
            events.filter {
                FavoritesManager.isFavorited(context, it.name)
            }.forEach {
                HackIllinoisNotificationManager.scheduleEventNotification(context, it)
            }
        }
    }
}
