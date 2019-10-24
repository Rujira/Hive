package com.codinghub.apps.hive.model.identifyparent

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class  IdentifyParentResponse(
    val name: String,
    val tel: String?,
    val gender: String?,
    val image: String?,
    val similarity: Double,
    val parent_pid: String,
    val students: List<ParentStudentData>,
    val ret: Int,
    val search_log_id: Int) : Parcelable
