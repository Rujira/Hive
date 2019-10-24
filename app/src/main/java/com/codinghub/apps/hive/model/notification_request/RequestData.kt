package com.codinghub.apps.hive.model.notification_request

data class RequestData(val id: String,
                       val parent_name: String,
                       val school_id: String,
                       val notify_parent: String,
                       val created_date: String,
                       val req_name: String,
                       val image: String,
                       val student_pid: String,
                       val student_name: String,
                       val student_image: String,
                       val notify_teacher: String,
                       val parent_pid: String,
                       val status: String)
