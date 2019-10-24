package com.codinghub.apps.hive.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentRequest
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentResponse
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.notifyparent.NotifyParentRequest
import com.codinghub.apps.hive.model.notifyparent.NotifyParentResponse

class FaceViewModel(application: Application): AndroidViewModel(application) {
    private val repository = Injection.provideRepository()

    fun identifyParent(picture: String, schoolId: String): LiveData<Either<IdentifyParentResponse>> {
        val request = IdentifyParentRequest(picture, schoolId)

        return repository.identifyParent(request)
    }

    fun notifyParent(school_id: String, parent_pid: String, search_log_id: Int, student_pid: List<String>): LiveData<Either<NotifyParentResponse>> {
        val request = NotifyParentRequest(
            school_id,
            parent_pid,
            search_log_id,
            student_pid
        )
        return repository.notifyParent(request)
    }

    fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap {
        return repository.modifyImageOrientation(activity, bitmap, uri)
    }
}