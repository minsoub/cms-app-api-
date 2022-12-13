package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page
import java.time.LocalDateTime
import java.util.*

@Schema(description = "게시글 리스트")
data class BoardResponse(
    val id: String,
    @Schema(description = "제목")
    val title: String,
    var categoryNames: List<String>? = null,
    var categoryId: List<String>? = null,
    val screenDate: LocalDateTime,
    val thumbnailUrl: String? = null,
)

fun CmsNotice.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryId = categoryId
)

fun CmsPressRelease.toResponse() = BoardResponse(
    id = id,
    title = title,
    screenDate = screenDate
)

data class DataResponse(
    val fix: List<BoardResponse>,
    val list: Page<BoardResponse>
)
