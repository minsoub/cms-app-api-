package com.bithumbsystems.cms.persistence.mongo.entity

import com.bithumbsystems.cms.persistence.mongo.enums.EventTarget
import com.bithumbsystems.cms.persistence.mongo.enums.EventType
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document("cms_event")
class CmsEvent(
    @MongoId
    val id: String,
    val title: String,
    val isFixTop: Boolean = false,
    val isShow: Boolean = true,
    val isDelete: Boolean = false,
    val content: String,
    val fileId: String? = null,
    val shareTitle: String? = null,
    val shareDescription: String? = null,
    val shareFileId: String? = null,
    val shareButtonName: String? = null,
    val isSchedule: Boolean? = false,
    val scheduleDate: LocalDateTime? = null,
    val isDraft: Boolean? = false,
    val readCount: Long = 0,
    val type: EventType,
    val target: EventTarget,
    val eventStartDate: LocalDateTime? = null,
    val eventEndDate: LocalDateTime? = null,
    val agreementContent: String? = null,
    val buttonName: String? = null,
    val buttonColor: String? = null,
    val buttonUrl: String? = null,
    val message: Message? = null,
    val createAccountId: String,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val updateAccountId: String? = null,
    val updateDate: LocalDateTime? = null,
    val useUpdateDate: Boolean = false,
    val isAlignTop: Boolean = false,
    val screenDate: LocalDateTime
)

class Message(
    val participateMessage: String,
    val duplicateMessage: String
)
