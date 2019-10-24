package com.codinghub.apps.hive.repository

import com.codinghub.apps.hive.model.notifications_message.MessageResponse
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentRequest
import com.codinghub.apps.hive.model.identifyparent.IdentifyParentResponse
import com.codinghub.apps.hive.model.login.LoginRequest
import com.codinghub.apps.hive.model.login.LoginResponse
import com.codinghub.apps.hive.model.myaccount.parent.*
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
import com.codinghub.apps.hive.model.student.student.StudentRequest
import com.codinghub.apps.hive.model.student.studentnew.NewStudentRequest
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse
import retrofit2.Call
import retrofit2.http.*

interface HiveApi  {

    //////////// Student Grade Room Module ///////////////////

    @Headers("Accept: application/json")
    @POST("school/login")
    fun schoolLogin(@Body body: LoginRequest): Call<LoginResponse>

    @Headers("Accept: application/json")
    @POST("school/grade")
    fun listGrade(@Body body: GradeRequest): Call<GradeResponse>

    @Headers("Accept: application/json")
    @POST("school/room")
    fun listRoom(@Body body: RoomRequest): Call<RoomResponse>

    @Headers("Accept: application/json")
    @POST("school/room/student")
    fun listStudent(@Body body: NewStudentRequest): Call<NewStudentResponse>

    @Headers("Accept: application/json")
    @POST("request_pickup")
    fun requestPickUp(@Body body: PickUpRequest): Call<PickUpResponse>

    @GET("student")
    fun getStudents(): Call<StudentResponse> // Alternate Version Not Use

    @GET("parent")
    fun getParents(): Call<ParentResponse> // Not Use

    //////////// Face Module ///////////////////

    @Headers("Accept: application/json")
    @POST("parent")
    fun addNewMember(@Body body: NewMemberRequest): Call<NewMemberResponse> // Not Use

    @Headers("Accept: application/json")
    @POST("identify/parent")
    fun identifyParent(@Body body: IdentifyParentRequest): Call<IdentifyParentResponse>

    @Headers("Accept: application/json")
    @POST("pickup")
    fun notifyParent(@Body body: NotifyParentRequest): Call<NotifyParentResponse>

    //////////// My Account Module ///////////////////

    @Headers("Accept: application/json")
    @POST("parent/user_info")
    fun getParentInfo(@Body body: ParentUserInfoRequest): Call<ParentUserInfoResponse>

    @Headers("Accept: application/json")
    @POST("update/parent")
    fun updateParentInformation(@Body body: UpdateParentRequest): Call<UpdateParentResponse>

    @Headers("Accept: application/json")
    @POST("add/sender")
    fun addNewSender(@Body body: AddSenderRequest): Call<AddSenderResponse>


    //////////// Notification Module ///////////////////

    @Headers("Accept: application/json")
    @POST("view/pickup")
    fun getPickUpMessagesForParent(@Body body: ParentMessageRequest): Call<ParentMessageResponse>

    @Headers("Accept: application/json")
    @POST("view/pickup")
    fun getPickUpMessagesForTeacher(@Body body: TeacherMessageRequest): Call<TeacherMessageResponse>

    @Headers("Accept: application/json")
    @POST("view/request_pickup")
    fun getRequestMessagesForTeacher(@Body body: TeacherPickupRequestRequest): Call<TeacherPickupRequestResponse>

    @Headers("Accept: application/json")
    @POST("view/request_pickup")
    fun getRequestMessagesForParent(@Body body: ParentPickupRequestRequest): Call<ParentPickupRequestResponse>

    @Headers("Accept: application/json")
    @POST("approve_pickup")
    fun approvePickup(@Body body: ApprovePickupRequest): Call<ApprovePickupResponse>

}