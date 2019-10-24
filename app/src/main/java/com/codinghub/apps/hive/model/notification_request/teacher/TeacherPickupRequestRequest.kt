package com.codinghub.apps.hive.model.notification_request.teacher

data class TeacherPickupRequestRequest(val teacher_pid: String,
                                       val role: String,
                                       val school_id: String)