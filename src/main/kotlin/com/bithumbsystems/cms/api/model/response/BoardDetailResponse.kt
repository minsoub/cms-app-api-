package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "게시글 상세")
data class BoardDetailResponse(
    val id: String,
    val title: String,
    var categoryNames: List<String>? = null,
    var categoryIds: List<String>? = null,
    val content: String,
    val screenDate: LocalDateTime,
    val shareTitle: String? = null,
    val shareDescription: String? = null,
    val shareFileId: String? = null,
    val shareButtonName: String? = null,
    val fileId: String? = null,
    var fileName: String? = null,
    var fileSize: Int? = null
)

fun CmsNotice.toDetailResponse() = BoardDetailResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryIds = categoryIds,
    content = content,
    shareTitle = shareTitle,
    shareDescription = shareDescription,
    shareFileId = shareFileId,
    shareButtonName = shareButtonName,
    fileId = fileId
)

fun CmsPressRelease.toDetailResponse() = BoardDetailResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    content = content,
    shareTitle = shareTitle,
    shareDescription = shareDescription,
    shareFileId = shareFileId,
    shareButtonName = shareButtonName,
    fileId = fileId
)
