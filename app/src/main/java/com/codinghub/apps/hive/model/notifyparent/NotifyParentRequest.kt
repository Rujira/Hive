package com.codinghub.apps.hive.model.notifyparent

data class NotifyParentRequest(val school_id: String,
                               val parent_pid: String,
                               val search_log_id: Int,
                               val student_pid: List<String>)

