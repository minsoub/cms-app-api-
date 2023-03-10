package com.bithumbsystems.cms.api.config.redis

import com.bithumbsystems.cms.api.config.aws.ParameterStoreConfig
import com.bithumbsystems.cms.api.config.client.ClientBuilder
import com.bithumbsystems.cms.api.util.PortCheckUtil.findAvailablePort
import com.bithumbsystems.cms.api.util.PortCheckUtil.isRunning
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.redisson.api.RedissonReactiveClient
import org.redisson.codec.JsonJacksonCodec
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import redis.embedded.RedisServer
import java.io.IOException
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Configuration
@Profile(value = ["local", "default", "test"])
class RedisConfig(
    val parameterStoreConfig: ParameterStoreConfig,
    val clientBuilder: ClientBuilder,
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

        val codec = JsonJacksonCodec()
        codec.objectMapper.registerKotlinModule()
        config.codec = codec

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
}
