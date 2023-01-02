package com.bithumbsystems.cms.persistence.redis

import com.bithumbsystems.cms.api.model.response.NoticeCategoryResponse
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeCategory
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeFix
import com.bithumbsystems.cms.persistence.redis.model.RedisReadCount
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RBucketReactive
import org.redisson.api.RMapCacheReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.stereotype.Service

@Service
class RedisOperator(
    private val redissonReactiveClient: RedissonReactiveClient,
    private val objectMapper: ObjectMapper,
    private val readCountRMapCache: RMapCacheReactive<String, MutableList<RedisReadCount>>,
) {
    suspend fun getTopNotice(): List<RedisNoticeFix> {
        val typeReference = object : TypeReference<List<RedisNoticeFix>>() {}

        return redissonReactiveClient
            .getBucket<List<RedisNoticeFix>>(RedisKey.REDIS_NOTICE_FIX_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))
            .get()
            .awaitSingle()
    }

    suspend fun setTopNotice(noticeList: List<RedisNoticeFix>): Void {
        val typeReference = object : TypeReference<List<RedisNoticeFix>>() {}

        val bucket: RBucketReactive<List<RedisNoticeFix>> = redissonReactiveClient
            .getBucket(RedisKey.REDIS_NOTICE_FIX_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))

        return bucket.set(noticeList).awaitSingle()
    }

    suspend fun getNoticeCategory(): List<NoticeCategoryResponse> {
        val typeReference = object : TypeReference<List<NoticeCategoryResponse>>() {}

        return redissonReactiveClient
            .getBucket<List<NoticeCategoryResponse>>(RedisKey.REDIS_NOTICE_CATEGORY_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))
            .get()
            .awaitSingle()
    }

    suspend fun setNoticeCategory(categoryList: List<RedisNoticeCategory>): Void {
        val typeReference = object : TypeReference<List<NoticeCategoryResponse>>() {}

        val bucket: RBucketReactive<List<RedisNoticeCategory>> = redissonReactiveClient
            .getBucket(RedisKey.REDIS_NOTICE_CATEGORY_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))

        return bucket.set(categoryList).awaitSingle()
    }

    suspend fun setReadCount(redisReadCount: RedisReadCount) {
        val typeReference = object : TypeReference<Map<String, MutableList<RedisReadCount>>>() {}
        val boardTypeReference = object : TypeReference<MutableList<RedisReadCount>>() {}

        val readCountMap: RMapCacheReactive<String, MutableList<RedisReadCount>> =
            redissonReactiveClient.getMapCache(RedisReadCountKey.REDIS_GLOBAL_READ_COUNT_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))

        var redisReadCountList =
            objectMapper.convertValue(readCountMap.get(RedisReadCountKey.REDIS_NOTICE_READ_COUNT_KEY).awaitSingleOrNull(), boardTypeReference)

        redisReadCount.readCount++

        if (redisReadCountList == null) {
            redisReadCountList = mutableListOf(redisReadCount)
        } else {
            if (redisReadCountList.any { it.id == redisReadCount.id }) {
                redisReadCountList.filter { it.id == redisReadCount.id }.map { it.readCount++ }
            } else {
                redisReadCountList.add(redisReadCount)
            }
        }
        readCountRMapCache.put(RedisReadCountKey.REDIS_NOTICE_READ_COUNT_KEY, redisReadCountList).awaitSingleOrNull()
    }
}
