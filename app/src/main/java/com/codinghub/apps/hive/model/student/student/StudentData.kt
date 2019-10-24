package com.codinghub.apps.hive.model.student.student

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudentData(val id: String?,
                       val parent_pid: String?,
                       val status: String?,
                       val updated_date: String?,
                       val school_id: String?,
                       val created_date: String?,
                       val room: String?,
                       val image: String?,
                       val student_pid: String?,
                       val number: Int?,
                       val teacher_pid: String?,
                       val grade: String?,
                       val gender: String?,
                       val name: String?) : Parcelable
