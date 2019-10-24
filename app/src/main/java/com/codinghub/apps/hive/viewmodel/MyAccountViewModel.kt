package com.codinghub.apps.hive.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.myaccount.parent.*

class MyAccountViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun getParentInfo(parent_id: String, school_id: String): LiveData<Either<ParentUserInfoResponse>> {
        val request = ParentUserInfoRequest(parent_id, school_id)
        return repository.getParentInfo(request)
    }

    fun updateParentInfo(name: String, parent_pid: String, picture: String, school_id: String, tel: String): LiveData<Either<UpdateParentResponse>> {
        val request = UpdateParentRequest(name, parent_pid, picture, school_id, tel)
        return repository.updateParentInformation(request)
    }

    fun addNewSender(name: String, parent_pid: String, picture: String, school_id: String, tel: String ): LiveData<Either<AddSenderResponse>> {
        val request = AddSenderRequest(name, parent_pid, picture, school_id, tel)
        return repository.addNewSender(request)
    }

    fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap {
        return repository.modifyImageOrientation(activity, bitmap, uri)
    }


}