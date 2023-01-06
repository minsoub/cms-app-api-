package com.bithumbsystems.cms.api.model.request

import com.bithumbsystems.cms.api.util.RedisRecentKey

data class BannerRequest(
    val boardType: RedisRecentKey,
    val pageSize: Int,
)
