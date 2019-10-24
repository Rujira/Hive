package com.codinghub.apps.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.login.LoginRequest
import com.codinghub.apps.hive.model.login.LoginResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun schoolLogin(username: String, password: String, school_id: String): LiveData<Either<LoginResponse>> {

        val request = LoginRequest(username, password, school_id)
        return repository.schoolLogin(request)
    }

    fun saveCurrentUser(currentUser: CurrentUser) {
        return AppPrefs.saveCurrentUser(currentUser)
    }

    fun saveLoginStatus(isUserLoggedIn: Boolean) {
        return AppPrefs.saveLoginStatus(isUserLoggedIn)
    }

    fun saveSchoolID(schoolID: String) {
        return AppPrefs.saveSchoolID(schoolID)
    }

    fun getLoginStatus(): Boolean {
        return AppPrefs.getLoginStatus()
    }

    fun registerUserForPushNotification(schoolID: String, userID: String, userRole:String) {
        return repository.registerUserForPushNotification(schoolID, userID, userRole)
    }



}