package org.hackillinois.android.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.hackillinois.android.R;
import org.hackillinois.android.database.entity.Event;

import java.util.Calendar;

public class HackIllinoisNotificationManager {

    private static final int MINUTES_BEFORE_TO_NOTIFY = 10;
    private static final int MILLIS_IN_MINUTE = 60 * 1000;
    private static final int REQUEST_CODE = 999999;

    private static final String CHANNEL_ID = "event_notification_channel";
    private static final String CHANNEL_NAME = "HackIllinois";

    public static void scheduleEventNotification(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Notification notification = buildEventNotification(context, event);
        PendingIntent alarmIntent = buildNotificationPendingIntent(context, notification);
        long timeToTrigger = event.getStartTimeMs() - MINUTES_BEFORE_TO_NOTIFY * MILLIS_IN_MINUTE;

        // don't set an alarm for an event that has already occurred
        if (event.getStartTimeMs() < Calendar.getInstance().getTimeInMillis()) { return; }
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeToTrigger, alarmIntent);
    }

    public static void cancelEventNotification(Context context, Event event) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Notification notification = buildEventNotification(context, event);
        PendingIntent alarmIntent = buildNotificationPendingIntent(context, notification);
        alarmManager.cancel(alarmIntent);
    }

    public static void runFirebaseInAppNotification(Context context, String title, String body) {
        Notification notification = buildGenericNotification(context, title, body);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private static PendingIntent buildNotificationPendingIntent(Context context, Notification notification) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("notification", notification);
        intent.putExtra("notification_id", 1);
        return PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static Notification buildEventNotification(Context context, Event event) {
        String title = event.getName();
        String body = "Event starts at " + event.getStartTimeOfDay() + ".";
        return buildGenericNotification(context, title, body);
    }

    private static Notification buildGenericNotification(Context context, String title, String body) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.logo);

        // In Oreo and above, every notification must be associated with a notification channel
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }
}
