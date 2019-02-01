package org.hackillinois.android.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.hackillinois.android.App
import org.hackillinois.android.common.HackIllinoisNotificationManager
import kotlin.concurrent.thread

class FirebaseNotificationMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        thread { App.getAPI().sendUserToken(DeviceToken(token)).execute() }
    }

    // this function is only called when the app is in the foreground and receives a notification
    override fun onMessageReceived(message: RemoteMessage?) {
        val title = message?.notification?.title ?: "Empty Title"
        val body = message?.notification?.body ?: "Empty Body"
        HackIllinoisNotificationManager.runFirebaseInAppNotification(this, title, body)
    }
}
