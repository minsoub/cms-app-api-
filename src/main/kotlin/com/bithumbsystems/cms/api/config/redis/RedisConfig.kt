package com.bithumbsystems.cms.api.config.redis

import com.bithumbsystems.cms.api.util.PortCheckUtil.findAvailablePort
import com.bithumbsystems.cms.api.util.PortCheckUtil.isRunning
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import org.redisson.Redisson
import org.redisson.api.MapOptions
import org.redisson.api.RMapCacheReactive
import org.redisson.api.RedissonReactiveClient
import org.redisson.api.map.MapWriter
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
class RedisConfig(
    private val redisProperties: RedisProperties,
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    private var redisServer: RedisServer? = null

    @PostConstruct
    @Throws(IOException::class)
    fun redisServer() {
        val redisPort = if (isRunning(redisProperties.port)) findAvailablePort() else redisProperties.port

        redisServer = RedisServer.builder()
            .port(redisPort)
            .setting("maxmemory 128M")
            .build()
        redisServer?.start()

        val config = Config()
        config.useSingleServer().address = "redis://${redisProperties.host}:$redisPort"
    }

    @PreDestroy
    fun stopRedis() {
        redisServer?.stop()
    }

    @Bean
    fun redissonReactiveClient(): RedissonReactiveClient =
        Redisson.create().reactive()

    @Bean
    fun readCountRMapCache(redissonReactiveClient: RedissonReactiveClient): RMapCacheReactive<String, Long> {
        return redissonReactiveClient.getMapCache(
            "CmsNoticeReadCount",
            MapOptions.defaults<String, Long>()
                .writer(getReadCountWriter())
                .writeMode(MapOptions.WriteMode.WRITE_BEHIND)
                .writeBehindBatchSize(5000)
                .writeBehindDelay(3000)
        )
    }

    @Transactional
    fun getReadCountWriter(): MapWriter<String, Long> {
        return object : MapWriter<String, Long> {
            override fun write(map: Map<String, Long>?) {
                map?.forEach {
                    reactiveMongoTemplate.findAndModify(
                        Query(Criteria.where("_id").`is`(it.key)),
                        Update().inc("read_count", 1),
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
