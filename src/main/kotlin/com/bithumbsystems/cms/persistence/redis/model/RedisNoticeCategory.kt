package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.api.model.response.NoticeCategoryResponse

data class RedisNoticeCategory(
    val id: String,
    val name: String
)

fun NoticeCategoryResponse.toRedisCategory() = RedisNoticeCategory(
    id = id,
    name = name
)
