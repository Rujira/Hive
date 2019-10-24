package com.codinghub.apps.hive.model.myaccount.parent

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParentInfoData(val gender: String?,
                          val id: Int,
                          val image: String?,
                          val name: String?,
                          val parent_pid: String?,
                          val password: String?,
                          val school_id: String,
                          val tel: String?) : Parcelable

