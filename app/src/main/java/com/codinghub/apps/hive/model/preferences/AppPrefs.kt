package com.codinghub.apps.hive.model.preferences

import android.preference.PreferenceManager
import com.codinghub.apps.hive.app.HiveApplication
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.student.grade.GradeData
import com.google.gson.Gson

object AppPrefs {

    private const val KEY_SERVICE_URL = "KEY_SERVICE_URL"
    private const val KEY_HEADER_USERNAME = "KEY_HEADER_USERNAME"
    private const val KEY_HEADER_PASSWORD = "KEY_HEADER_PASSWORD"
    private const val KEY_SCHOOL_ID = "KEY_SCHOOL_ID"
    private const val KEY_CURRENT_USER = "KEY_CURRENT_USER"
    private const val KEY_IS_USER_LOGGED_IN = "KEY_IS_USER_LOGGED_IN"
    private const val KEY_IMAGE_PATH = "KEY_IMAGE_PATH"
    private const val KEY_THUMBNAIL_PATH = "KEY_THUMBNAIL_PATH"
    private const val KEY_NOTIFICATION_FLAG = "KEY_NOTIFICATION_FLAG"
    private const val KEY_NOTIFICATION_STATE = "KEY_NOTIFICATION_STATE"
    private const val KEY_KIOSK_AUTO_SNAP_MODE = "KEY_KIOSK_AUTO_SNAP_MODE"
    private const val KEY_IS_SHOW_BOUNDING_BOX = "KEY_IS_SHOW_BOUNDING_BOX"
    private const val KEY_DISTANCE = "KEY_DISTANCE"
    private const val KEY_SELECTED_GRADE = "KEY_SELECTED_GRADE"

    private val gson = Gson()

    private fun sharedPrefs() = PreferenceManager.getDefaultSharedPreferences(HiveApplication.getAppContext())

    fun getServiceURL(): String? = sharedPrefs().getString(KEY_SERVICE_URL, "http://27.254.41.63:8040/")  //http://115.31.144.251:8040
    fun getHeaderUserName(): String? = sharedPrefs().getString(KEY_HEADER_USERNAME, "CDHDev_Hive")
    fun getHeaderPassword(): String? = sharedPrefs().getString(KEY_HEADER_PASSWORD, "hive1234")

    fun getImagePath(): String? = sharedPrefs().getString(KEY_IMAGE_PATH, "image/")
    fun getThumbnailPath(): String? = sharedPrefs().getString(KEY_THUMBNAIL_PATH, "thumbnail/")

    fun saveSchoolID(schoolID: String) {
        sharedPrefs().edit().putString(KEY_SCHOOL_ID, schoolID).apply()
    }

    fun getSchoolID(): String? = sharedPrefs().getString(KEY_SCHOOL_ID, "XINGMIN")

    fun removeSchoolID() {
        sharedPrefs().edit().remove(KEY_SCHOOL_ID).apply()
    }

    fun saveCurrentUser(currentUser: CurrentUser) {
        sharedPrefs().edit().putString(KEY_CURRENT_USER, gson.toJson(currentUser)).apply()
    }

    fun getCurrentUser(): List<CurrentUser> {
        return sharedPrefs().all.keys
            .map { sharedPrefs().getString(KEY_CURRENT_USER, "") }
            .filterNot { it.isNullOrBlank() }
            .map { gson.fromJson(it, CurrentUser::class.java) }
    }

    fun removeCurrentUser() {
        sharedPrefs().edit().remove(KEY_CURRENT_USER).apply()
    }

    fun saveLoginStatus(isUserLoggedIn: Boolean) {
        sharedPrefs().edit().putBoolean(KEY_IS_USER_LOGGED_IN, isUserLoggedIn).apply()
    }

    fun getLoginStatus(): Boolean = sharedPrefs().getBoolean(KEY_IS_USER_LOGGED_IN, false)

    fun saveSelectedGrade(grade: GradeData) {
        sharedPrefs().edit().putString(KEY_SELECTED_GRADE, gson.toJson(grade)).apply()
    }

    fun getSelectedGrade(): List<GradeData> {
        return sharedPrefs().all.keys
            .map { sharedPrefs().getString(KEY_SELECTED_GRADE, "") }
            .filterNot { it.isNullOrBlank() }
            .map { gson.fromJson(it, GradeData::class.java) }
    }

    fun removeSelectedGrade() {
        sharedPrefs().edit().remove(KEY_SELECTED_GRADE).apply()
    }

    fun saveNotificationFlag(topic: String) {
        sharedPrefs().edit().putString(KEY_NOTIFICATION_FLAG, topic).apply()
    }

    fun getNotificationFlag(): String? = sharedPrefs().getString(KEY_NOTIFICATION_FLAG, "")

    fun saveNotificationState(state: Boolean) {
        sharedPrefs().edit().putBoolean(KEY_NOTIFICATION_STATE, state).apply()
    }

    fun getNotificationState(): Boolean = sharedPrefs().getBoolean(KEY_NOTIFICATION_STATE, true)

    fun saveKioskAutoSnapMode(isEnable: Boolean) {
        sharedPrefs().edit().putBoolean(KEY_KIOSK_AUTO_SNAP_MODE, isEnable).apply()
    }

    fun getKioskAutoSnapMode(): Boolean = sharedPrefs().getBoolean(KEY_KIOSK_AUTO_SNAP_MODE, false)

    fun saveShowBoundingBoxState(isShowBoundingBox: Boolean) {
        sharedPrefs().edit().putBoolean(KEY_IS_SHOW_BOUNDING_BOX, isShowBoundingBox).apply()
    }

    fun getShowBoundingBoxState(): Boolean = sharedPrefs().getBoolean(KEY_IS_SHOW_BOUNDING_BOX, true)

    fun saveSnapDistance(distance: Int) {
        sharedPrefs().edit().putInt(KEY_DISTANCE, distance).apply()
    }

    fun getSnapDistance(): Int = sharedPrefs().getInt(KEY_DISTANCE, 50000)
}