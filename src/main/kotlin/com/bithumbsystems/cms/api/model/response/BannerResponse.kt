package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.*
import com.bithumbsystems.cms.persistence.redis.model.RedisBanner

open class BannerResponse(
    val id: String,
    val title: String
)

fun RedisBanner.toResponse() = BannerResponse(
    id = id,
    title = title
)

fun CmsNotice.toBannerResponse() = BannerResponse(
    id = id,
    title = title
)
