package org.hackillinois.android2019.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.hackillinois.android2019.R;
import org.hackillinois.android2019.database.entity.Event;
import org.hackillinois.android2019.notifications.HackIllinoisNotificationManager;

public class FavoritesManager {

    public static void favoriteEvent(Context context, Event event) {
        setBoolean(context, event.getName(), true);
        HackIllinoisNotificationManager.scheduleEventNotification(context, event);
    }

    public static void unfavoriteEvent(Context context, Event event) {
        setBoolean(context, event.getName(), false);
        HackIllinoisNotificationManager.cancelEventNotification(context, event);
    }

    public static boolean isFavorited(Context context, String eventName) {
        return getBoolean(context, eventName);
    }

    private static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getFavoritesPrefs(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getBoolean(Context context, String key) {
        return getFavoritesPrefs(context).getBoolean(key, false);
    }

    private static SharedPreferences getFavoritesPrefs(Context context) {
        return context.getSharedPreferences(context.getString(R.string.favorites_pref_file_key), Context.MODE_PRIVATE);
    }
}
