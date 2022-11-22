package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import java.time.LocalDateTime
import java.time.ZoneOffset

data class BoardResponse(
    val categoryId: List<String>,
    val title: String,
    val isFixTop: Boolean = false,
    val isShow: Boolean = true,
    val isDelete: Boolean = false,
    val isBanner: Boolean = false,
    val content: String? = null,
    val fileId: String? = null,
    val shareTitle: String? = null,
    val shareDescription: String? = null,
    val shareFileId: String? = null,
    val shareButtonName: String? = null,
    val isSchedule: Boolean = false,
    val scheduleDate: Long? = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val isDraft: Boolean = false,
    val createAccountId: String,
    val createDate: Long? = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
)

fun BoardResponse.toEntity() = CmsNotice(
    categoryId, title, isFixTop, isShow, isDelete, isBanner, content, fileId,
    shareTitle, shareDescription, shareFileId, shareButtonName, isSchedule, scheduleDate, isDraft, createAccountId
)

fun CmsNotice.toResponse() = BoardResponse(
    categoryId, title, isFixTop, isShow, isDelete, isBanner, content, fileId,
    shareTitle, shareDescription, shareFileId, shareButtonName, isSchedule, scheduleDate, isDraft, createAccountId
)
