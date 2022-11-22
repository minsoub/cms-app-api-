package com.bithumbsystems.cms.persistence.mongo.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime
import java.util.*

@Document("cms_notice")
class CmsNotice(
    @MongoId
    val id: String = UUID.randomUUID().toString(),
    val categoryId: List<String>,
    val title: String,
    val isFixTop: Boolean = false,
    val isShow: Boolean = true,
    val isDelete: Boolean = false,
    val isBanner: Boolean = false,
    val content: String,
    val fileId: String? = null,
    val shareTitle: String? = null,
    val shareDescription: String? = null,
    val shareFileId: String? = null,
    val shareButtonName: String? = null,
    val isSchedule: Boolean = false,
    val scheduleDate: LocalDateTime? = null,
    val isDraft: Boolean = false,
    val readCount: Long = 0,
    val createAccountId: String,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val updateAccountId: String? = null,
    val updateDate: LocalDateTime? = null,
    val useUpdateDate: Boolean = false,
    val isAlignTop: Boolean = false,
    val screenDate: LocalDateTime
)
