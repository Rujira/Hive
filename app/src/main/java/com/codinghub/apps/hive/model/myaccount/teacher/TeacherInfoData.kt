package com.codinghub.apps.hive.model.myaccount.teacher

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TeacherInfoData(val grade: String?,
                           val id: Int,
                           val image: String?,
                           val line: String?,
                           val mail: String?,
                           val name: String?,
                           val room: String?,
                           val school_id: String,
                           val teacher_pid: String,
                           val tel: String?) : Parcelable
