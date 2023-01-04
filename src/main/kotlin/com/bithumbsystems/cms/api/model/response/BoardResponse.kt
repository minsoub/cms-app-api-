package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEvent
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeFix
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import java.time.LocalDateTime

@Schema(description = "게시글 리스트")
data class BoardResponse(
    val id: String,
    @Schema(description = "제목")
    val title: String,
    var categoryName: List<String>? = null,
    var categoryIds: List<String>? = null,
    val screenDate: LocalDateTime? = null,
    val thumbnailUrl: String? = null,
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

fun RedisNoticeFix.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryName = categoryName
)

data class DataResponse(
    val fix: List<BoardResponse>,
    val list: Page<BoardResponse>
)
