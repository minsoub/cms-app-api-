package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import java.util.*

@Schema(description = "게시글 리스트")
data class BoardResponse(
    val id: String,
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
