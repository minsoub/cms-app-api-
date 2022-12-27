package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.api.model.response.BoardDetailResponse

data class RedisReadCount(
    val id: String,
    var readCount: Long
) {
    constructor() : this("", 0)
}

fun BoardDetailResponse.toRedisReadCount() = RedisReadCount(
    id = id,
    readCount = readCount
)
