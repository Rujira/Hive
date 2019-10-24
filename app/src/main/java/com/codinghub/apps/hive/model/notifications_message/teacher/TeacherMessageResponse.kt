package com.codinghub.apps.hive.model.notifications_message.teacher

import com.codinghub.apps.hive.model.notifications_message.MessageData

data class TeacherMessageResponse(val ret: Int,
                                  val data: List<MessageData>)