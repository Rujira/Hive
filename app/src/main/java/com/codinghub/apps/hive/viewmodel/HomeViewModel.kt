package com.codinghub.apps.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.preferences.AppPrefs

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    fun getCurrentUser(): List<CurrentUser> {
        return AppPrefs.getCurrentUser()
    }
}