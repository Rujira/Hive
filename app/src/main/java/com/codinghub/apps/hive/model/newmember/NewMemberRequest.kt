package com.codinghub.apps.hive.model.newmember

data class NewMemberRequest(val picture: String,
                            val name: String,
                            val parent_pid: String,
                            val school_id: String,
                            val tel: String)
