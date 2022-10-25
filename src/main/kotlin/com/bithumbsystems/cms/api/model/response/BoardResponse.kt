package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.api.model.mongo.entity.CmsNotice

class BoardResponse(
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

fun CmsNotice.toResponse() = BoardResponse(
    categoryId, title, isFixTop, isShow, isBanner, content, fileId, shareTitle, shareDescription, scheduleDate, isDraft, createAccountId, createDate, updateAccountId, updateDate
)
