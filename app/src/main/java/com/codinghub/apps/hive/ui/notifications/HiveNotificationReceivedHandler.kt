package com.codinghub.apps.hive.ui.notifications

import android.util.Log
import com.onesignal.OSNotification
import com.onesignal.OneSignal

class HiveNotificationReceivedHandler : OneSignal.NotificationReceivedHandler {
    private val TAG = HiveNotificationReceivedHandler::class.qualifiedName

    override fun notificationReceived(notification: OSNotification?) {
        Log.d(TAG, "On Notification Received")
    }
}