package org.hackillinois.android.notifications

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.hackillinois.android.App
import org.hackillinois.android.R

object FirebaseTokenManager {
    private const val FIREBASE_TOKEN_KEY = "firebaseToken"
    private const val FIREBASE_NEW_KEY = "isNew"
    private const val DEFAULT_FIREBASE_TOKEN = ""

    fun writeToken(context: Context, token: String) {
        setIsTokenNew(context, true)
        val sharedPrefKey = context.getString(R.string.authorization_pref_file_key)
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putString(FIREBASE_TOKEN_KEY, token)
        editor.apply()
    }

    fun readToken(context: Context): String {
        val sharedPrefKey = context.getString(R.string.authorization_pref_file_key)
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return editor.getString(FIREBASE_TOKEN_KEY, DEFAULT_FIREBASE_TOKEN) ?: DEFAULT_FIREBASE_TOKEN
    }

    fun sendTokenToServerIfNew(context: Context) {
        if (isTokenNew(context)) {
            val token = readToken(context)
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    App.getAPI().sendUserToken(DeviceToken(token))
                    writeToken(context, DEFAULT_FIREBASE_TOKEN)
                    setIsTokenNew(context, false)
                } catch (e: Exception) {
                    Log.e("sendTokenToServer", e.toString())
                }
            }
        }
    }

    private fun setIsTokenNew(context: Context, isNew: Boolean) {
        val sharedPrefKey = context.getString(R.string.authorization_pref_file_key)
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE).edit()
        editor.putBoolean(FIREBASE_NEW_KEY, isNew)
        editor.apply()
    }

    private fun isTokenNew(context: Context): Boolean {
        val sharedPrefKey = context.getString(R.string.authorization_pref_file_key)
        val editor = context.getSharedPreferences(sharedPrefKey, Context.MODE_PRIVATE)
        return editor.getBoolean(FIREBASE_NEW_KEY, true)
    }
}
