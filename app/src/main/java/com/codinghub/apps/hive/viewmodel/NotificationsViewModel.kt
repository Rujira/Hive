package com.codinghub.apps.hive.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.notifications_message.MessageResponse
import com.codinghub.apps.hive.model.login.CurrentUser
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.myaccount.parent.ParentUserInfoResponse
import com.codinghub.apps.hive.model.notification_request.ApprovePickupRequest
import com.codinghub.apps.hive.model.notification_request.ApprovePickupResponse
import com.codinghub.apps.hive.model.notification_request.RequestResponse
import com.codinghub.apps.hive.model.notification_request.parent.ParentPickupRequestRequest
import com.codinghub.apps.hive.model.notification_request.parent.ParentPickupRequestResponse
import com.codinghub.apps.hive.model.notification_request.teacher.TeacherPickupRequestRequest
import com.codinghub.apps.hive.model.notification_request.teacher.TeacherPickupRequestResponse
import com.codinghub.apps.hive.model.notifications_message.parent.ParentMessageRequest
import com.codinghub.apps.hive.model.notifications_message.parent.ParentMessageResponse
import com.codinghub.apps.hive.model.notifications_message.teacher.TeacherMessageRequest
import com.codinghub.apps.hive.model.notifications_message.teacher.TeacherMessageResponse
import com.codinghub.apps.hive.model.preferences.AppPrefs

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()

    fun getCurrentUser(): List<CurrentUser> {

        return AppPrefs.getCurrentUser()
    }

    fun getMessagesForParent(parent_pid: String, role: String, schoolId: String): LiveData<Either<ParentMessageResponse>> {

        val request = ParentMessageRequest(parent_pid, role, schoolId)

        return repository.getPickUpMessagesForParent(request)
    }

    fun getMessagesForTeacher(teacher_pid: String, role: String, schoolId: String): LiveData<Either<TeacherMessageResponse>> {

        val request = TeacherMessageRequest(teacher_pid, role, schoolId)

        return repository.getPickUpMessagesForTeacher(request)
    }

    fun getRequestForParent(parent_pid: String, role: String, schoolId: String): LiveData<Either<ParentPickupRequestResponse>> {

        val request = ParentPickupRequestRequest(parent_pid, role, schoolId)

        return repository.getRequestMessagesForParent(request)
    }

    fun getRequestForTeacher(teacher_pid: String, role: String, schoolId: String): LiveData<Either<TeacherPickupRequestResponse>> {

        val request = TeacherPickupRequestRequest(teacher_pid, role, schoolId)

        return repository.getRequestMessagesForTeacher(request)
    }

    fun approvePickup(approve: String, request_id: String, school_id: String) : LiveData<Either<ApprovePickupResponse>> {

        val request = ApprovePickupRequest(approve, request_id, school_id)

        return repository.approvePickup(request)
    }
}

