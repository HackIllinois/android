package org.hackillinois.android.notifications

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * This class receives a broadcast from the Android alarm system with the notification packaged into the intent
 * This receiver's responsibility is to publish this notification as soon as it gets it
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtra<Notification>("notification") as Notification
        val id = intent.getIntExtra("notification_id", 0)
        notificationManager.notify(id, notification)
    }
}
