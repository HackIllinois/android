package org.hackillinois.android.common;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import org.hackillinois.android.R;
import org.hackillinois.android.model.Event;

public class HackIllinoisNotificationManager {

    private static final int MINUTES_BEFORE_TO_NOTIFY = 10;
    private static final int MILLIS_IN_MINUTE = 60 * 1000;
    private static final int REQUEST_CODE = 999999;

    public static void scheduleNotification(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = buildPendingIntent(context, event);
        long timeToTrigger = event.getStartTimeMs() - MINUTES_BEFORE_TO_NOTIFY * MILLIS_IN_MINUTE;
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeToTrigger, alarmIntent);
    }

    public static void cancelNotification(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = buildPendingIntent(context, event);
        alarmManager.cancel(alarmIntent);
    }

    private static PendingIntent buildPendingIntent(Context context, Event event) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        Notification notification = buildNotification(context, event);
        intent.putExtra("notification", notification);
        intent.putExtra("notification_id", 1);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Notification buildNotification(Context context, Event event) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(event.getName());
        builder.setContentText("Event starts at " + event.getStartTimeOfDay() + ".");
        builder.setSmallIcon(R.drawable.logo);

        // In Oreo and above, every notification must be associated with a notification channel
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            final String CHANNEL_ID = "event_notification_channel";
            final String name = "HackIllinois";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }
}
