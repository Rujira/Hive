package com.codinghub.apps.hive.model.myaccount.parent

import com.codinghub.apps.hive.model.student.studentnew.NewStudentData

data class ParentUserInfoResponse(val parent_info: ParentInfoData,
                                  val senders: List<SenderData>,
                                  val students: List<NewStudentData>,
                                  val ret: Int)