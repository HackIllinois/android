package org.hackillinois.android.common;

import android.content.Context;
import android.content.SharedPreferences;

import org.hackillinois.android.R;

public class FavoritesManager {

    public static void favoriteEvent(Context context, String eventName) {
        setBoolean(context, eventName, true);
    }

    public static void unfavoriteEvent(Context context, String eventName) {
        setBoolean(context, eventName, false);
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
