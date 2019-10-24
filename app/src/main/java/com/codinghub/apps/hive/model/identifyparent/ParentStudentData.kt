package com.codinghub.apps.hive.model.identifyparent

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParentStudentData(val id: Int,
                             val name: String?,
                             val parent_pid: String?,
                             val image: String?,
                             val room: String?,
                             val student_pid: String?,
                             val teacher_pid: String?,
                             val grade: String?,
                             val gender: String?) : Parcelable

