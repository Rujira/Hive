package com.codinghub.apps.hive.ui.notifications

import android.content.Intent
import android.util.Log
import com.codinghub.apps.hive.app.HiveApplication
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.ui.main.MainActivity
import com.onesignal.OSNotificationAction
import com.onesignal.OSNotificationOpenResult
import com.onesignal.OneSignal

class HiveNotificationOpenHandler : OneSignal.NotificationOpenedHandler {

    private val TAG = HiveNotificationOpenHandler::class.qualifiedName

    companion object {
        const val INTENT_OPEN_NOTIFICATION_KEY = "topic"
    }

    override fun notificationOpened(result: OSNotificationOpenResult?) {

        if (AppPrefs.getLoginStatus()) {

            Log.d(TAG, "Notification TAPPED")

            val actionType = result?.action?.type
            val data = result?.notification?.payload?.additionalData

            Log.d(TAG, "Notification Data ${data?.optString("topic")}")

            val customKey = data?.optString("topic", null)
            if (customKey != null) {

                Log.d(TAG, "customkey set with value: $customKey")

                val intent = Intent(HiveApplication.instance, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra(INTENT_OPEN_NOTIFICATION_KEY, customKey)
                HiveApplication.instance.startActivity(intent)

            }
        }
    }

}