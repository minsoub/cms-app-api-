package com.bithumbsystems.cms.api.util

object RedisKey {
    const val REDIS_NOTICE_FIX_KEY = "CMS_NOTICE_FIX"
    const val REDIS_NOTICE_CATEGORY_KEY = "CMS_NOTICE_CATEGORY"
}

object RedisReadCountKey {
    const val REDIS_GLOBAL_READ_COUNT_KEY = "CMS_READ_COUNT"
    const val REDIS_NOTICE_READ_COUNT_KEY = "CMS_NOTICE_READ_COUNT"
}
