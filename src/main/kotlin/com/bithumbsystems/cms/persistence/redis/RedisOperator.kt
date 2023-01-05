package com.bithumbsystems.cms.persistence.redis

import com.bithumbsystems.cms.api.model.response.NoticeCategoryResponse
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.redis.model.RedisNoticeCategory
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.redisson.api.RBucketReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.stereotype.Service

@Service
class RedisOperator(
    private val redissonReactiveClient: RedissonReactiveClient,
    private val objectMapper: ObjectMapper,
) {
    suspend fun <T> getTopList(redisKey: String, typeReference: TypeReference<List<T>>): List<T> {
        return redissonReactiveClient
            .getBucket<List<T>>(redisKey, TypedJsonJacksonCodec(typeReference, objectMapper))
            .get()
            .awaitSingle()
    }

    suspend fun <T> setTopList(redisKey: String, topList: List<T>, clazz: Class<T>): Void {
        val bucket: RBucketReactive<List<T>> = redissonReactiveClient
            .getBucket(redisKey, TypedJsonJacksonCodec(clazz, objectMapper))

        return bucket.set(topList).awaitSingle()
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
}
