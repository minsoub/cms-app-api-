package com.bithumbsystems.cms.persistence.redis

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import kotlinx.coroutines.reactor.awaitSingle
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.TypedJsonJacksonCodec
import org.springframework.stereotype.Service

@Service
class RedisOperator(
//    private val reactiveRedisTemplate: ReactiveStringRedisTemplate
    private val redissonReactiveClient: RedissonReactiveClient
) {
    companion object {
        private const val TOP_KEY = "top"
    }

    suspend fun getTopNotice(): CmsNotice? =
        redissonReactiveClient.getBucket<CmsNotice>(TOP_KEY, TypedJsonJacksonCodec(CmsNotice::class.java))
            .get()
            .awaitSingle()

    suspend fun setTopNotice(cmsNotice: CmsNotice): Void? =
        redissonReactiveClient.getBucket<CmsNotice>(TOP_KEY).set(cmsNotice).awaitSingle()

//
//    suspend fun getTopNotice(): CmsNotice? =
//        Gson().fromJson(reactiveRedisTemplate.opsForValue().get(TOP_KEY).awaitSingle(), CmsNotice::class.java)
//
//    suspend fun setTopNotice(cmsNotice: CmsNotice) {
//        reactiveRedisTemplate.opsForValue().set(TOP_KEY, Gson().toJson(cmsNotice)).awaitSingle()
//    }
}
