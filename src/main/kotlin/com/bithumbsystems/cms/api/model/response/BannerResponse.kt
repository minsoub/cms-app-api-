package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import com.bithumbsystems.cms.persistence.redis.model.RedisBanner

open class BannerResponse(
    val id: String,
    val title: String
)

fun RedisBanner.toResponse() = BannerResponse(
    id = id,
    title = title
)

fun CmsNotice.toBannerResponse(categoryMap: Map<String, String>) = BannerResponse(
    id = id,
    title = categoryMap[id]?.let { "[$it] $title" } ?: title
)

fun CmsPressRelease.toBannerResponse() = BannerResponse(
    id = id,
    title = title
)
