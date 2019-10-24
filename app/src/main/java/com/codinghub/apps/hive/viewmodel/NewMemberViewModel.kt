package com.codinghub.apps.hive.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.newmember.NewMemberRequest
import com.codinghub.apps.hive.model.newmember.NewMemberResponse

class NewMemberViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun addNewParent(picture: String,name: String, parent_pid: String, school_id: String, tel: String): LiveData<Either<NewMemberResponse>> {
        val request =
            NewMemberRequest(picture, name, parent_pid, school_id, tel)

        return repository.addNewMember(request)
    }

    fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap {
        return repository.modifyImageOrientation(activity, bitmap, uri)
    }
}

