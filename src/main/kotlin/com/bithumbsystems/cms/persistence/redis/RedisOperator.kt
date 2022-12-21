package com.bithumbsystems.cms.persistence.redis

import com.bithumbsystems.cms.api.model.response.NoticeCategoryResponse
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeCategory
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeFix
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kotlinx.coroutines.reactor.awaitSingle
import org.redisson.api.RBucketReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RedisOperator(
    private val redissonReactiveClient: RedissonReactiveClient,
) {
    suspend fun getTopNotice(): List<RedisNoticeFix> {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val typeReference = object : TypeReference<List<RedisNoticeFix>>() {}

        return redissonReactiveClient
            .getBucket<List<RedisNoticeFix>>(RedisKey.REDIS_NOTICE_FIX_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))
            .get()
            .awaitSingle()
    }

    suspend fun setTopNotice(noticeList: List<RedisNoticeFix>): Void {

        val objectMapper = ObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val bucket: RBucketReactive<List<RedisNoticeFix>> = redissonReactiveClient
            .getBucket(RedisKey.REDIS_NOTICE_FIX_KEY, TypedJsonJacksonCodec(List::class.java, LocalDateTime::class.java, objectMapper))

        return bucket.set(noticeList).awaitSingle()
    }

    suspend fun getNoticeCategory(): List<NoticeCategoryResponse> {
        val objectMapper = ObjectMapper()
        val typeReference = object : TypeReference<List<NoticeCategoryResponse>>() {}

        return redissonReactiveClient
            .getBucket<List<NoticeCategoryResponse>>(RedisKey.REDIS_NOTICE_CATEGORY_KEY, TypedJsonJacksonCodec(typeReference, objectMapper))
            .get()
            .awaitSingle()
    }

    suspend fun setNoticeCategory(categoryList: List<RedisNoticeCategory>): Void {
        val bucket: RBucketReactive<List<RedisNoticeCategory>> = redissonReactiveClient
            .getBucket(RedisKey.REDIS_NOTICE_CATEGORY_KEY, TypedJsonJacksonCodec(List::class.java))

        return bucket.set(categoryList).awaitSingle()
    }
}
