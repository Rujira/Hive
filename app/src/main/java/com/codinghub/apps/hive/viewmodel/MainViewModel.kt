package com.codinghub.apps.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.grade.GradeRequest
import com.codinghub.apps.hive.model.student.grade.GradeResponse

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun getCurrentUser(): List<CurrentUser> {
        return AppPrefs.getCurrentUser()
    }

    fun removeCurrentUser() {
        return AppPrefs.removeCurrentUser()
    }

    fun removeSchoolID() {
        return AppPrefs.removeSchoolID()
    }

    fun saveLoginStatus(isUserLoggedIn: Boolean) {
        return AppPrefs.saveLoginStatus(isUserLoggedIn)
    }

    fun unregisterUserForPushNotification() = repository.unregisterUserForPushNotification()

    fun listGrade(school_id: String): LiveData<Either<GradeResponse>> {
        val request = GradeRequest(school_id)
        return repository.listGrade(request)
    }

}
