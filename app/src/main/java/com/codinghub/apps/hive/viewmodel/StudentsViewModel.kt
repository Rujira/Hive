package com.codinghub.apps.hive.viewmodel

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.codinghub.apps.hive.app.Injection
import com.codinghub.apps.hive.model.error.Either
import com.codinghub.apps.hive.model.pickuprequest.PickUpRequest
import com.codinghub.apps.hive.model.pickuprequest.PickUpResponse
import com.codinghub.apps.hive.model.student.grade.GradeRequest
import com.codinghub.apps.hive.model.student.grade.GradeResponse
import com.codinghub.apps.hive.model.student.room.RoomRequest
import com.codinghub.apps.hive.model.student.room.RoomResponse
import com.codinghub.apps.hive.model.student.studentnew.NewStudentRequest
import com.codinghub.apps.hive.model.student.studentnew.NewStudentResponse

class StudentsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Injection.provideRepository()
  //  private val allStudents = repository.getStudents()

    fun getStudents() = repository.getStudents()

    fun requestPickUp(picture: String, request_name: String, school_id: String, student_pid: List<String>): LiveData<Either<PickUpResponse>> {
        val request =
            PickUpRequest(picture, request_name, school_id, student_pid)
        return repository.requestPickUp(request)
    }

    fun modifyImageOrientation(activity: Activity, bitmap: Bitmap, uri: Uri): Bitmap {
        return repository.modifyImageOrientation(activity, bitmap, uri)
    }

    fun listGrade(school_id: String): LiveData<Either<GradeResponse>> {
        val request = GradeRequest(school_id)
        return repository.listGrade(request)
    }

    fun listRoom(school_id: String, grade: String): LiveData<Either<RoomResponse>> {
        val request = RoomRequest(school_id, grade)
        return repository.listRoom(request)
    }

    fun listStudent(school_id: String, grade: String, room: String): LiveData<Either<NewStudentResponse>> {
        val request = NewStudentRequest(school_id, grade, room)
        return repository.listStudent(request)
    }

}

