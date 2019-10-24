package com.codinghub.apps.hive.model.student.room

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomData(val grade: String, val name: String, val room: String): Parcelable

