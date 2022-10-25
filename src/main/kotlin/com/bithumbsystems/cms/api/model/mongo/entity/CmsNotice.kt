package com.bithumbsystems.cms.api.model.mongo.entity

import java.util.UUID

class CmsNotice(
    val id: String? = UUID.randomUUID().toString().replace("-", ""),
    val categoryId: String,
    val title: String,
    val isFixTop: Boolean,
    val isShow: Boolean,
    val isBanner: Boolean,
    val content: String,
    val fileId: String,
    val shareTitle: String,
    val shareDescription: String,
    val scheduleDate: Long,
    val isDraft: Boolean,
    val createAccountId: String,
    val createDate: Long,
    val updateAccountId: String?,
    val updateDate: Long
)
