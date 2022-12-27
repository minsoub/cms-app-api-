package com.bithumbsystems.cms.api.config.redis

import com.bithumbsystems.cms.api.config.aws.ParameterStoreConfig
import com.bithumbsystems.cms.api.config.client.ClientBuilder
import com.bithumbsystems.cms.api.util.PortCheckUtil.findAvailablePort
import com.bithumbsystems.cms.api.util.PortCheckUtil.isRunning
import com.bithumbsystems.cms.api.util.RedisReadCountKey
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.model.RedisReadCount
import org.redisson.api.MapOptions
import org.redisson.api.RMapCacheReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.api.map.MapWriter
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.transaction.annotation.Transactional
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@Profile(value = ["local", "default", "test"])
class RedisConfig(
    val parameterStoreConfig: ParameterStoreConfig,
    val clientBuilder: ClientBuilder,
    private val reactiveMongoTemplate: ReactiveMongoTemplate,
    val noticeRepository: CmsNoticeRepository
) {

    private var redisServer: RedisServer? = null
    private val config = Config()

    @PostConstruct
    @Throws(IOException::class)
    fun redisServer() {
        val redisPort =
            if (isRunning(parameterStoreConfig.redisProperties.port)) findAvailablePort()
            else parameterStoreConfig.redisProperties.port

        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory 128M")
            .build()
        redisServer?.start()

        config.useSingleServer().address = "redis://${parameterStoreConfig.redisProperties.host}:$redisPort"
        if (!parameterStoreConfig.redisProperties.token.isNullOrEmpty()) {
            config.useSingleServer().password = parameterStoreConfig.redisProperties.token
        }
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }

    @Bean
    fun redissonReactiveClient(): RedissonReactiveClient = clientBuilder.buildRedis(config)

    @Bean
    fun readCountRMapCache(redissonReactiveClient: RedissonReactiveClient): RMapCacheReactive<String, MutableList<RedisReadCount>>? {
        return redissonReactiveClient.getMapCache(
            RedisReadCountKey.REDIS_GLOBAL_READ_COUNT_KEY,
            MapOptions.defaults<String, MutableList<RedisReadCount>>()
                .writer(getReadCountWriter())
                .writeMode(MapOptions.WriteMode.WRITE_BEHIND)
                .writeBehindBatchSize(5000)
                .writeBehindDelay(3000)
        )
    }

    @Transactional
    fun getReadCountWriter(): MapWriter<String, MutableList<RedisReadCount>> {
        return object : MapWriter<String, MutableList<RedisReadCount>> {
            override fun write(map: Map<String, MutableList<RedisReadCount>>?) {
                map?.forEach {
                    reactiveMongoTemplate.updateFirst(
                        Query.query(Criteria.where("_id").`is`("1643340")),
                        Update.update("read_count", 1),
                        CmsNotice::class.java
                    ).subscribe()
                }
            }

            override fun delete(keys: MutableCollection<String>?) {
                TODO("Not yet implemented")
            }
        }
    }
}
