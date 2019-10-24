package com.codinghub.apps.hive.model.notifications_message.teacher

data class TeacherMessageRequest(val teacher_pid: String,
                                 val role: String,
                                 val school_id: String)