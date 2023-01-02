package com.bithumbsystems.cms.api.util

object RedisKey {
    const val REDIS_NOTICE_FIX_KEY = "CMS_NOTICE_FIX"
    const val REDIS_PRESS_RELEASE_FIX_KEY = "CMS_PRESS_RELEASE_FIX"
    const val REDIS_EVENT_FIX_KEY = "CMS_EVENT_FIX"
    const val REDIS_REVIEW_REPORT_FIX_KEY = "CMS_REVIEW_REPORT_FIX"
    const val REDIS_INVESTMENT_WARNING_FIX_KEY = "CMS_INVESTMENT_WARNING_FIX"
    const val REDIS_ECONOMIC_RESEARCH_FIX_KEY = "CMS_ECONOMIC_RESEARCH_FIX"
    const val REDIS_NOTICE_CATEGORY_KEY = "CMS_NOTICE_CATEGORY"
}

object RedisReadCountKey {
    const val REDIS_GLOBAL_READ_COUNT_KEY = "CMS_READ_COUNT"
    const val REDIS_NOTICE_READ_COUNT_KEY = "CMS_NOTICE_READ_COUNT"
}
