package com.codinghub.apps.hive.repository

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.model.notifications_message.MessageResponse
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentRequest
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentResponse
import com.codinghub.apps.hive.model.login.LoginRequest
import com.codinghub.apps.hive.model.login.LoginResponse
import com.codinghub.apps.hive.model.myaccount.parent.*
import com.codinghub.apps.hive.model.myaccount.teacher.TeacherUserInfoRequest
import com.codinghub.apps.hive.model.myaccount.teacher.TeacherUserInfoResponse
import com.codinghub.apps.hive.model.parent.ParentResponse
import com.codinghub.apps.hive.model.newmember.NewMemberRequest
import com.codinghub.apps.hive.model.newmember.NewMemberResponse
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
import com.codinghub.apps.hive.model.notifyparent.NotifyParentRequest
import com.codinghub.apps.hive.model.notifyparent.NotifyParentResponse
import com.codinghub.apps.hive.model.pickuprequest.PickUpRequest
import com.codinghub.apps.hive.model.pickuprequest.PickUpResponse
import com.codinghub.apps.hive.model.student.student.StudentResponse
import com.codinghub.apps.hive.model.student.grade.GradeRequest
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.model.student.room.RoomRequest
import com.codinghub.apps.hive.model.student.room.RoomResponse
import com.codinghub.apps.hive.model.student.studentnew.NewStudentRequest
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse

interface Repository {

    fun schoolLogin(request: LoginRequest): LiveData<Either<LoginResponse>>

    fun listGrade(request: GradeRequest): LiveData<Either<GradeResponse>>
    fun listRoom(request: RoomRequest): LiveData<Either<RoomResponse>>
    fun listStudent(request: NewStudentRequest): LiveData<Either<NewStudentResponse>>

    fun getTeacherInfo(request: TeacherUserInfoRequest): LiveData<Either<TeacherUserInfoResponse>>

    fun getParentInfo(request: ParentUserInfoRequest): LiveData<Either<ParentUserInfoResponse>>
    fun updateParentInformation(request: UpdateParentRequest): LiveData<Either<UpdateParentResponse>>
    fun addNewSender(request: AddSenderRequest): LiveData<Either<AddSenderResponse>>

    fun addNewMember(request: NewMemberRequest): LiveData<Either<NewMemberResponse>>
    fun identifyParent(request: IdentifyParentRequest): LiveData<Either<IdentifyParentResponse>>
    fun notifyParent(request: NotifyParentRequest): LiveData<Either<NotifyParentResponse>>

    fun getStudents(): LiveData<Either<StudentResponse>>
    fun getParents(): LiveData<Either<ParentResponse>>

    fun requestPickUp(request: PickUpRequest): LiveData<Either<PickUpResponse>>

    fun getPickUpMessagesForTeacher(request: TeacherMessageRequest): LiveData<Either<TeacherMessageResponse>>
    fun getPickUpMessagesForParent(request: ParentMessageRequest): LiveData<Either<ParentMessageResponse>>

    fun getRequestMessagesForTeacher(request: TeacherPickupRequestRequest): LiveData<Either<TeacherPickupRequestResponse>>
    fun getRequestMessagesForParent(request: ParentPickupRequestRequest): LiveData<Either<ParentPickupRequestResponse>>

    fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap

    fun approvePickup(request: ApprovePickupRequest): LiveData<Either<ApprovePickupResponse>>

    fun registerUserForPushNotification(schoolID:String, userID:String, userRole:String)
    fun unregisterUserForPushNotification()
}