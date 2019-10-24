package com.codinghub.apps.hive.model.pickuprequest

data class PickUpRequest(val picture: String,
                         val request_name: String,
                         val school_id: String,
                         val student_pid: List<String>)

