package com.codinghub.apps.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.preferences.AppPrefs

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun registerUserForPushNotification(schoolID: String, userID: String, userRole:String) {
        return repository.registerUserForPushNotification(schoolID, userID, userRole)
    }

    fun unregisterUserForPushNotification() = repository.unregisterUserForPushNotification()

    fun saveNotificationState(state: Boolean) {
        AppPrefs.saveNotificationState(state)
    }

    fun saveKioskAutoSnapMode(isEnable: Boolean) {
        AppPrefs.saveKioskAutoSnapMode(isEnable)
    }

    fun saveShowBoundingBoxState(isShowBoundingBox: Boolean) {
        AppPrefs.saveShowBoundingBoxState(isShowBoundingBox)
    }

    fun saveSnapDistance(distance: Int) {
        AppPrefs.saveSnapDistance(distance)
    }
}