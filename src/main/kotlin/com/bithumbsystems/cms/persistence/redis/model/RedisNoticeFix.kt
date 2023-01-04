package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.api.model.response.BoardResponse
import java.time.LocalDateTime

data class RedisNoticeFix(
    val id: String,
    val title: String,
    val screenDate: LocalDateTime? = null,
    val categoryNames: List<String>? = null,
    val thumbnailUrl: String? = null
)

fun BoardResponse.toNoticeFix() = RedisNoticeFix(
    id = id,
    title = title,
    screenDate = screenDate,
    categoryNames = categoryNames,
    thumbnailUrl = thumbnailUrl
)
