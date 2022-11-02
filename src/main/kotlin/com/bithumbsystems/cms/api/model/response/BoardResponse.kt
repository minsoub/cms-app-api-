package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice

data class BoardResponse(
    val categoryId: String,
    val title: String,
    val isFixTop: Boolean,
    val isShow: Boolean,
    val isBanner: Boolean,
    val content: String,
    val fileId: String?,
    val shareTitle: String,
    val shareDescription: String,
    val scheduleDate: Long?,
    val isDraft: Boolean,
    val createAccountId: String,
    val createDate: Long,
    val updateAccountId: String?,
    val updateDate: Long?
) {
    fun toEntity(): CmsNotice = CmsNotice(
        categoryId, title, content, shareTitle, shareDescription, scheduleDate, createAccountId, createDate
    )
}

fun CmsNotice.toResponse() = BoardResponse(
    categoryId, title, isFixTop, isShow, isBanner, content, fileId, shareTitle,
    shareDescription, scheduleDate, isDraft, createAccountId, createDate, updateAccountId, updateDate
)
