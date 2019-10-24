package com.codinghub.apps.hive.model.notifications_message

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MessageData(val created_date: String,
                       val id: Int,
                       val image: String,
                       val notify_parent: String,
                       val notify_teacher: String,
                       val parent_name: String,
                       val parent_pid: String,
                       val school_id: String,
                       val student_pid: String,
                       val student_name: String,
                       val student_image: String) : Parcelable
