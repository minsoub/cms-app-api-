package com.bithumbsystems.cms.persistence.redis.model

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNoticeCategory

data class RedisNoticeCategory(
    val id: String,
    val name: String
)

fun CmsNoticeCategory.toRedisCategory() = RedisNoticeCategory(
    id = id,
    name = name
)
