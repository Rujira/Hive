package com.codinghub.apps.hive.model.notifications_message.parent

import com.codinghub.apps.hive.model.notifications_message.MessageData

data class ParentMessageResponse(val ret: Int,
                                 val data: List<MessageData>)