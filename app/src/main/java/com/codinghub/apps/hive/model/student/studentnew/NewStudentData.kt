package com.codinghub.apps.hive.model.student.studentnew

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewStudentData(val id: Int,
                       val parent_pid: String?,
                       val status: String?,
                       val school_id: String?,
                       val room: String?,
                       val image: String?,
                       val student_pid: String?,
                       val number: Int?,
                       val teacher_pid: String?,
                       val grade: String?,
                       val gender: String?,
                       val name: String?) : Parcelable
