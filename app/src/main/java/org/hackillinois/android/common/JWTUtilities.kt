package org.hackillinois.android.common

import android.content.Context
import android.content.SharedPreferences
import org.hackillinois.android.R

object JWTUtilities {
    const val DEFAULT_JWT = ""
    const val JWT_PREF_KEY = "jwt"

    fun readJWT(context: Context): String {
        val sharedPreferences = getSharedPreferences(context)
        return sharedPreferences.getString(JWT_PREF_KEY, DEFAULT_JWT) ?: DEFAULT_JWT
    }

    fun writeJWT(context: Context, jwt: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString("jwt", jwt)
        editor.apply()
    }

    fun clearJWT(context: Context) {
        writeJWT(context, DEFAULT_JWT)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        val prefKey = context.applicationContext.getString(R.string.authorization_pref_file_key)
        return context.getSharedPreferences(prefKey, Context.MODE_PRIVATE)
    }
}
