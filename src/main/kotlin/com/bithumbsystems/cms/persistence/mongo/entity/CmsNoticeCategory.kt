package com.bithumbsystems.cms.persistence.mongo.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document("cms_notice_category")
class CmsNoticeCategory(
    @MongoId
    val id: String,
    val name: String,
    val isUse: Boolean = true,
    val createAccountId: String,
    val createDate: LocalDateTime = LocalDateTime.now(),
    val updateAccountId: String? = null,
    val updateDate: LocalDateTime? = null
)
