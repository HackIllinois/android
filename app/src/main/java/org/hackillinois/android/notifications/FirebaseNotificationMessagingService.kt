package org.hackillinois.android.notifications

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.hackillinois.android.R

class FirebaseNotificationMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        val editor = applicationContext.getSharedPreferences(applicationContext.getString(R.string.authorization_pref_file_key), Context.MODE_PRIVATE).edit()
        editor.putString("firebaseToken", token)
        editor.apply()
    }

    // this function is only called when the app is in the foreground and receives a notification
    override fun onMessageReceived(message: RemoteMessage?) {
        val title = message?.notification?.title ?: "Empty Title"
        val body = message?.notification?.body ?: "Empty Body"
        HackIllinoisNotificationManager.runFirebaseInAppNotification(this, title, body)
    }
}
