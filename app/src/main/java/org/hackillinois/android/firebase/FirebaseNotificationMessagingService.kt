package org.hackillinois.android.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import org.hackillinois.android.App
import kotlin.concurrent.thread

class FirebaseNotificationMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        thread { App.getAPI().sendUserToken(DeviceToken(token)).execute() }
    }
}
