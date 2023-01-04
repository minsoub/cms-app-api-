package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.api.model.response.BoardDetailResponse
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice

data class RedisReadCount(
    val id: String,
    var readCount: Long
)

fun BoardDetailResponse.toRedisReadCount() = RedisReadCount(
    id = id,
    readCount = readCount
)

fun CmsNotice.toRedisReadCount() = RedisReadCount(
    id = id,
    readCount = readCount
)
