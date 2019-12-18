package com.codinghub.apps.hive.ui.notifications

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.ui.notifications.messages.MessageListFragment
import com.onesignal.OSNotification
import com.onesignal.OneSignal

class HiveNotificationReceivedHandler(val context: Context) : OneSignal.NotificationReceivedHandler {

    private val TAG = HiveNotificationReceivedHandler::class.qualifiedName

    override fun notificationReceived(notification: OSNotification?) {
        Log.d(TAG, "On Notification Received")

        if (AppPrefs.getLoginStatus()) {

            Log.d(TAG, "Notification Received ${notification?.payload?.additionalData?.optString("topic")}")

            when (notification?.payload?.additionalData?.optString("topic")) {
                "REQUEST" -> {
                    Log.d(TAG, "Request Received")

                    val intent = Intent("notification-broadcast")
                    intent.putExtra("topic", "REQUEST")
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

                }
                "PICKUP" -> {
                    Log.d(TAG, "Pickup Received")

                    val intent = Intent("notification-broadcast")
                    intent.putExtra("topic", "PICKUP")
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                }
            }
        }
    }
}