package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.api.util.getS3Url
import com.bithumbsystems.cms.persistence.mongo.entity.*
import com.bithumbsystems.cms.persistence.redis.model.RedisBoard
import com.bithumbsystems.cms.persistence.redis.model.RedisNotice
import com.bithumbsystems.cms.persistence.redis.model.RedisThumbnail
import org.springframework.data.domain.Page
import java.time.LocalDateTime

open class BoardResponse(
    val id: String,
    val title: String,
    val createDate: LocalDateTime
)

class NoticeResponse(
    id: String,
    title: String,
    createDate: LocalDateTime,
    var categoryIds: List<String>?
) : BoardResponse(id, title, createDate)

class NoticeFixResponse(
    id: String,
    title: String,
    createDate: LocalDateTime,
    var categoryNames: List<String>?
) : BoardResponse(id, title, createDate)

class BoardThumbnailResponse(
    id: String,
    title: String,
    createDate: LocalDateTime,
    var thumbnailUrl: String?
) : BoardResponse(id, title, createDate)

fun CmsNotice.toResponse() = NoticeResponse(
    id = id,
    title = title,
    createDate = createDate,
    categoryIds = categoryIds
)

fun CmsPressRelease.toResponse() = BoardResponse(
    id = id,
    title = title,
    createDate = createDate
)

fun CmsEvent.toResponse() = BoardResponse(
    id = id,
    title = title,
    createDate = createDate
)

fun CmsReviewReport.toResponse() = BoardThumbnailResponse(
    id = id,
    title = title,
    createDate = createDate,
    thumbnailUrl = thumbnailUrl ?: thumbnailFileId?.getS3Url()
)

fun CmsEconomicResearch.toResponse() = BoardThumbnailResponse(
    id = id,
    title = title,
    createDate = createDate,
    thumbnailUrl = thumbnailUrl ?: thumbnailFileId?.getS3Url()
)

fun CmsInvestmentWarning.toResponse() = BoardResponse(
    id = id,
    title = title,
    createDate = createDate
)

fun RedisNotice.toResponse() = NoticeFixResponse(
    id = id,
    title = title,
    createDate = createDate,
    categoryNames = categoryNames
)

fun RedisBoard.toResponse() = BoardResponse(
    id = id,
    title = title,
    createDate = createDate
)

fun RedisThumbnail.toResponse() = BoardThumbnailResponse(
    id = id,
    title = title,
    createDate = createDate,
    thumbnailUrl = thumbnailUrl
)

data class DataResponse<T>(
    val fix: List<T>,
    val list: Page<T>
)
