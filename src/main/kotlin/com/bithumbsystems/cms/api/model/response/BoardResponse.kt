package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.api.util.getS3Url
import com.bithumbsystems.cms.persistence.mongo.entity.*
import com.bithumbsystems.cms.persistence.redis.model.RedisBoardFixList
import org.springframework.data.domain.Page
import java.time.LocalDateTime

data class BoardResponse(
    val id: String?,
    val title: String?,
    var categoryNames: List<String>? = null,
    var categoryIds: List<String>? = null,
    val screenDate: LocalDateTime? = null,
    var thumbnailUrl: String? = null,
    val thumbnailFileId: String? = null,
)

fun CmsNotice.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryIds = categoryIds
)

fun CmsPressRelease.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate
)

fun CmsEvent.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate
)

fun CmsReviewReport.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    thumbnailUrl = thumbnailFileId?.getS3Url()
)

fun CmsEconomicResearch.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate
)

fun CmsInvestmentWarning.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate
)

fun RedisBoardFixList.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryNames = categoryNames
)

data class DataResponse(
    val fix: List<BoardResponse>,
    val list: Page<BoardResponse>
)
