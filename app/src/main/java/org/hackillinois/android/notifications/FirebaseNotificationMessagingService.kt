package org.hackillinois.android.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseNotificationMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        FirebaseTokenManager.writeToken(applicationContext, token)
        FirebaseTokenManager.sendTokenToServerIfNew(applicationContext)
    }

    // this function is only called when the app is in the foreground and receives a notification
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message?.notification?.title ?: "Empty Title"
        val body = message?.notification?.body ?: "Empty Body"
        HackIllinoisNotificationManager.runFirebaseInAppNotification(this, title, body)
    }
}
