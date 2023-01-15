package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.api.model.response.BannerResponse
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.BoardThumbnailResponse
import com.bithumbsystems.cms.api.model.response.NoticeFixResponse
import java.time.LocalDateTime

data class RedisBoard(
    val id: String,
    val title: String,
    val createDate: LocalDateTime
)

data class RedisNotice(
    val id: String,
    val title: String,
    val categoryNames: List<String>?,
    val createDate: LocalDateTime
)

data class RedisThumbnail(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val createDate: LocalDateTime
)

data class RedisBanner(
    val id: String,
    val title: String // [카테고리] title
)

fun BoardResponse.toRedis() = RedisBoard(
    id = id,
    title = title,
    createDate = createDate,
)

fun NoticeFixResponse.toRedis() = RedisNotice(
    id = id,
    title = title,
    categoryNames = categoryNames,
    createDate = createDate,
)

fun BoardThumbnailResponse.toRedis() = RedisThumbnail(
    id = id,
    title = title,
    thumbnailUrl = thumbnailUrl,
    createDate = createDate,
)

fun BannerResponse.toRedis() = RedisBanner(
    id = id,
    title = title
)
