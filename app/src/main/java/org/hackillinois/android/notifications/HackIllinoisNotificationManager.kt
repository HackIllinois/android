package org.hackillinois.android.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import org.hackillinois.android.R
import org.hackillinois.android.database.entity.Event
import java.util.*

object HackIllinoisNotificationManager {
    private val MINUTES_BEFORE_TO_NOTIFY = 10
    private val MILLIS_IN_MINUTE = 60 * 1000
    private val REQUEST_CODE = 999999

    private val CHANNEL_ID = "event_notification_channel"
    private val CHANNEL_NAME = "HackIllinois"

    fun scheduleEventNotification(context: Context, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notification = buildEventNotification(context, event)
        val alarmIntent = buildNotificationPendingIntent(context, notification)
        val timeToTrigger = event.getStartTimeMs() - MINUTES_BEFORE_TO_NOTIFY * MILLIS_IN_MINUTE

        // set an alarm only if the event starts in the future
        if (event.getStartTimeMs() >= Calendar.getInstance().timeInMillis) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeToTrigger, alarmIntent)
        }
    }

    fun cancelEventNotification(context: Context, event: Event) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notification = buildEventNotification(context, event)
        val alarmIntent = buildNotificationPendingIntent(context, notification)
        alarmManager.cancel(alarmIntent)
    }

    fun runFirebaseInAppNotification(context: Context, title: String, body: String) {
        val notification = buildGenericNotification(context, title, body)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    private fun buildNotificationPendingIntent(context: Context, notification: Notification): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification", notification)
            putExtra("notification_id", 1)
        }
        val flags = PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, flags)
    }

    private fun buildEventNotification(context: Context, event: Event): Notification {
        val title = event.name
        val body = "Event starts at ${event.getStartTimeOfDay()}."
        return buildGenericNotification(context, title, body)
    }

    private fun buildGenericNotification(context: Context, title: String, body: String): Notification {
        val builder = Notification.Builder(context).apply {
            setContentTitle(title)
            setContentText(body)
            setSmallIcon(R.drawable.logo)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.logo))
        }

        // In Oreo and above, every notification must be associated with a notification channel
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(CHANNEL_ID)
        }

        return builder.build()
    }
}
