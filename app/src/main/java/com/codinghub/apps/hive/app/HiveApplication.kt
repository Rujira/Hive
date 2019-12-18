package com.codinghub.apps.hive.app

import android.app.Application
import android.content.Context
import com.codinghub.apps.hive.ui.notifications.HiveNotificationOpenHandler
import com.codinghub.apps.hive.ui.notifications.HiveNotificationReceivedHandler
import com.codinghub.apps.hive.ui.notifications.messages.MessageListFragment
import com.onesignal.OneSignal

class HiveApplication : Application() {
    companion object {
        lateinit var instance: HiveApplication

        fun getAppContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        super.onCreate()

      //  OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.WARN) //Remove when release

        OneSignal.startInit(this)
            .setNotificationOpenedHandler(HiveNotificationOpenHandler())
            .setNotificationReceivedHandler(HiveNotificationReceivedHandler(this))
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}