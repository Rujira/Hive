package com.codinghub.apps.hive.repository

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.notifications_message.MessageResponse
import com.codinghub.apps.hive.model.error.ApiError
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
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.model.student.student.StudentResponse
import com.codinghub.apps.hive.model.student.grade.GradeRequest
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.model.student.room.RoomRequest
import com.codinghub.apps.hive.model.student.room.RoomResponse
import com.codinghub.apps.hive.model.student.studentnew.NewStudentRequest
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse
import com.onesignal.OneSignal
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RemoteRepository: Repository {

    private val api = Injection.provideHiveApi()

    private val TAG = RemoteRepository::class.qualifiedName

    override fun schoolLogin(request: LoginRequest): LiveData<Either<LoginResponse>> {

        val liveData = MutableLiveData<Either<LoginResponse>>()
        api.schoolLogin(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.SCHOOL_LOGIN, null)
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.SCHOOL_LOGIN, null)
            }
        })

        return liveData
    }

    override fun listGrade(request: GradeRequest): LiveData<Either<GradeResponse>> {
        val liveData = MutableLiveData<Either<GradeResponse>>()
        api.listGrade(request).enqueue(object : Callback<GradeResponse> {
            override fun onResponse(call: Call<GradeResponse>, response: Response<GradeResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.LIST_GRADE, null)
                }
            }
            override fun onFailure(call: Call<GradeResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.LIST_GRADE, null)
            }
        })

        return liveData
    }

    override fun listRoom(request: RoomRequest): LiveData<Either<RoomResponse>> {
        val liveData = MutableLiveData<Either<RoomResponse>>()
        api.listRoom(request).enqueue(object : Callback<RoomResponse> {
            override fun onResponse(call: Call<RoomResponse>, response: Response<RoomResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.LIST_ROOM, null)
                }
            }
            override fun onFailure(call: Call<RoomResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.LIST_ROOM, null)
            }
        })

        return liveData
    }

    override fun listStudent(request: NewStudentRequest): LiveData<Either<NewStudentResponse>> {
        val liveData = MutableLiveData<Either<NewStudentResponse>>()
        api.listStudent(request).enqueue(object : Callback<NewStudentResponse> {
            override fun onResponse(call: Call<NewStudentResponse>, response: Response<NewStudentResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.LIST_STUDENT, null)
                }
            }
            override fun onFailure(call: Call<NewStudentResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.LIST_STUDENT, null)
            }
        })

        return liveData
    }

    override fun getTeacherInfo(request: TeacherUserInfoRequest): LiveData<Either<TeacherUserInfoResponse>> {
        val liveData = MutableLiveData<Either<TeacherUserInfoResponse>>()
        api.getTeacherInfo(request).enqueue(object : Callback<TeacherUserInfoResponse> {
            override fun onResponse(call: Call<TeacherUserInfoResponse>, response: Response<TeacherUserInfoResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_TEACHER_INFO, null)
                }
            }
            override fun onFailure(call: Call<TeacherUserInfoResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_TEACHER_INFO, null)
            }
        })

        return liveData
    }

    override fun getParentInfo(request: ParentUserInfoRequest): LiveData<Either<ParentUserInfoResponse>> {
        val liveData = MutableLiveData<Either<ParentUserInfoResponse>>()
        api.getParentInfo(request).enqueue(object : Callback<ParentUserInfoResponse> {
            override fun onResponse(call: Call<ParentUserInfoResponse>, response: Response<ParentUserInfoResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_PARENT_INFO, null)
                }
            }
            override fun onFailure(call: Call<ParentUserInfoResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_PARENT_INFO, null)
            }
        })

        return liveData
    }

    override fun updateParentInformation(request: UpdateParentRequest): LiveData<Either<UpdateParentResponse>> {
        val liveData = MutableLiveData<Either<UpdateParentResponse>>()
        api.updateParentInformation(request).enqueue(object : Callback<UpdateParentResponse> {
            override fun onResponse(call: Call<UpdateParentResponse>, response: Response<UpdateParentResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.UPDATE_PARENT_INFO, null)
                }
            }
            override fun onFailure(call: Call<UpdateParentResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.UPDATE_PARENT_INFO, null)
            }
        })

        return liveData
    }

    override fun addNewSender(request: AddSenderRequest): LiveData<Either<AddSenderResponse>> {
        val liveData = MutableLiveData<Either<AddSenderResponse>>()
        api.addNewSender(request).enqueue(object : Callback<AddSenderResponse> {
            override fun onResponse(call: Call<AddSenderResponse>, response: Response<AddSenderResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.ADD_SENDER, null)
                }
            }
            override fun onFailure(call: Call<AddSenderResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.ADD_SENDER, null)
            }
        })

        return liveData
    }

    override fun addNewMember(request: NewMemberRequest): LiveData<Either<NewMemberResponse>> {

        val liveData = MutableLiveData<Either<NewMemberResponse>>()
        api.addNewMember(request).enqueue(object : Callback<NewMemberResponse> {

            override fun onResponse(call: Call<NewMemberResponse>, response: Response<NewMemberResponse>) {
                if(response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.NEW_PARENT, null)
                }
            }

            override fun onFailure(call: Call<NewMemberResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.NEW_PARENT, null)
            }
        })

        return liveData
    }



    override fun identifyParent(request: IdentifyParentRequest): LiveData<Either<IdentifyParentResponse>> {

        val liveData = MutableLiveData<Either<IdentifyParentResponse>>()
        api.identifyParent(request).enqueue(object : Callback<IdentifyParentResponse> {

            override fun onResponse(call: Call<IdentifyParentResponse>, response: Response<IdentifyParentResponse>) {
                if(response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.IDENTIFY_PARENT, null)
                }
            }

            override fun onFailure(call: Call<IdentifyParentResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.IDENTIFY_PARENT, null)
            }

        })

        return liveData
    }

    override fun getStudents(): LiveData<Either<StudentResponse>> {

        val liveData = MutableLiveData<Either<StudentResponse>>()
        api.getStudents().enqueue(object : Callback<StudentResponse> {

            override fun onResponse(call: Call<StudentResponse>, response: Response<StudentResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.STUDENTS, null)
                }
            }

            override fun onFailure(call: Call<StudentResponse>, t: Throwable) {
               liveData.value = Either.error(ApiError.STUDENTS, null)
            }

        })

        return liveData
    }

    override fun getParents(): LiveData<Either<ParentResponse>> {

        val liveData = MutableLiveData<Either<ParentResponse>>()
        api.getParents().enqueue(object : Callback<ParentResponse> {

            override fun onResponse(call: Call<ParentResponse>, response: Response<ParentResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.PARENTS, null)
                }
            }

            override fun onFailure(call: Call<ParentResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.PARENTS, null)
            }

        })

        return liveData
    }

    override fun notifyParent(request: NotifyParentRequest): LiveData<Either<NotifyParentResponse>> {

        val liveData = MutableLiveData<Either<NotifyParentResponse>>()
        api.notifyParent(request).enqueue(object : Callback<NotifyParentResponse> {

            override fun onResponse(call: Call<NotifyParentResponse>, response: Response<NotifyParentResponse>) {
                if(response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.NOTIFY_PARENT, null)
                }
            }

            override fun onFailure(call: Call<NotifyParentResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.NOTIFY_PARENT, null)
            }

        })

        return liveData
    }


    override fun requestPickUp(request: PickUpRequest): LiveData<Either<PickUpResponse>> {

        val liveData = MutableLiveData<Either<PickUpResponse>>()
        api.requestPickUp(request).enqueue(object : Callback<PickUpResponse> {

            override fun onResponse(call: Call<PickUpResponse>, response: Response<PickUpResponse>) {
                if(response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.PICK_UP_REQUEST, null)
                }
            }

            override fun onFailure(call: Call<PickUpResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.PICK_UP_REQUEST, null)
            }

        })

        return liveData
    }

    override fun getPickUpMessagesForTeacher(request: TeacherMessageRequest): LiveData<Either<TeacherMessageResponse>> {

        val liveData = MutableLiveData<Either<TeacherMessageResponse>>()
        api.getPickUpMessagesForTeacher(request).enqueue(object : Callback<TeacherMessageResponse> {

            override fun onResponse(call: Call<TeacherMessageResponse>, response: Response<TeacherMessageResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
                }
            }

            override fun onFailure(call: Call<TeacherMessageResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
            }

        })

        return liveData
    }

    override fun getPickUpMessagesForParent(request: ParentMessageRequest): LiveData<Either<ParentMessageResponse>> {

        val liveData = MutableLiveData<Either<ParentMessageResponse>>()
        api.getPickUpMessagesForParent(request).enqueue(object : Callback<ParentMessageResponse> {

            override fun onResponse(call: Call<ParentMessageResponse>, response: Response<ParentMessageResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
                }
            }

            override fun onFailure(call: Call<ParentMessageResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
            }

        })

        return liveData
    }

    override fun getRequestMessagesForTeacher(request: TeacherPickupRequestRequest): LiveData<Either<TeacherPickupRequestResponse>> {

        val liveData = MutableLiveData<Either<TeacherPickupRequestResponse>>()
        api.getRequestMessagesForTeacher(request).enqueue(object : Callback<TeacherPickupRequestResponse> {

            override fun onResponse(call: Call<TeacherPickupRequestResponse>, response: Response<TeacherPickupRequestResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
                }
            }

            override fun onFailure(call: Call<TeacherPickupRequestResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
            }

        })

        return liveData
    }

    override fun getRequestMessagesForParent(request: ParentPickupRequestRequest): LiveData<Either<ParentPickupRequestResponse>> {

        val liveData = MutableLiveData<Either<ParentPickupRequestResponse>>()
        api.getRequestMessagesForParent(request).enqueue(object : Callback<ParentPickupRequestResponse> {

            override fun onResponse(call: Call<ParentPickupRequestResponse>, response: Response<ParentPickupRequestResponse>) {
                if (response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
                }
            }

            override fun onFailure(call: Call<ParentPickupRequestResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.GET_PICK_UP_MESSAGE, null)
            }

        })

        return liveData

    }

    override fun approvePickup(request: ApprovePickupRequest): LiveData<Either<ApprovePickupResponse>> {

        val liveData = MutableLiveData<Either<ApprovePickupResponse>>()
        api.approvePickup(request).enqueue(object : Callback<ApprovePickupResponse> {

            override fun onResponse(call: Call<ApprovePickupResponse>, response: Response<ApprovePickupResponse>) {
                if(response.isSuccessful) {
                    liveData.value = Either.success(response.body())
                } else {
                    liveData.value = Either.error(ApiError.APPROVE_PICK_UP_REQUEST, null)
                }
            }

            override fun onFailure(call: Call<ApprovePickupResponse>, t: Throwable) {
                liveData.value = Either.error(ApiError.APPROVE_PICK_UP_REQUEST, null)
            }

        })

        return liveData

    }

    @SuppressLint("Recycle")
    override fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap {

        val columns = arrayOf(MediaStore.MediaColumns.DATA)
        val c = activity.contentResolver.query(uri, columns, null, null, null)
        if (c == null) {
            Log.d("modifyOrientation", "Could not get cursor")
            return bitmap
        }

        c.moveToFirst()
        Log.d("modifyOrientation", c.getColumnName(0))
        val str = c.getString(0)
        if (str == null) {
            Log.d("modifyOrientation", "Could not get exif")
            return bitmap
        }
        Log.d("modifyOrientation", "get cursor");
        val exifInterface = ExifInterface(c.getString(0)!!)
        val exifR : Int = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        val orientation : Float =
            when (exifR) {
                ExifInterface.ORIENTATION_ROTATE_90 ->  90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

        val mat : Matrix? = Matrix()
        mat?.postRotate(orientation)
        return Bitmap.createBitmap(bitmap as Bitmap, 0, 0, bitmap?.width as Int,
            bitmap.height as Int, mat, true)
    }

    override fun registerUserForPushNotification(schoolID: String, userID: String, userRole: String) {

        val tags = JSONObject()
        tags.put("pid", "${schoolID}:${userID}")
        tags.put("role", "${schoolID}:${userRole}")
        OneSignal.sendTags(tags)
        OneSignal.setSubscription(true)
    }

    override fun unregisterUserForPushNotification() {

        OneSignal.removeExternalUserId()
        OneSignal.setSubscription(false)

        val tagList = ArrayList<String>()
        tagList.add("pid")
        tagList.add("role")
        OneSignal.deleteTags(tagList)
    }
}