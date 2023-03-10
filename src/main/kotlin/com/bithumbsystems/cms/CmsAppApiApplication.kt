package com.bithumbsystems.cms

import org.redisson.spring.starter.RedissonAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.context.ApplicationPidFileWriter
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    exclude = [
        MongoAutoConfiguration::class,
        MongoReactiveAutoConfiguration::class,
        MongoDataAutoConfiguration::class,
        EmbeddedMongoAutoConfiguration::class,
        RedissonAutoConfiguration::class
    ]
)
@ConfigurationPropertiesScan(
    basePackages = ["com.bithumbsystems.cms"]
)
class CmsAppApiApplication

fun main(args: Array<String>) {
    runApplication<CmsAppApiApplication>(*args) {
//        val serviceLoader = ServiceLoader.load(BlockHoundIntegration::class.java)
//        BlockHound.builder()
//            .apply { serviceLoader.forEach { with(it) } }
//            .install()
        addListeners(ApplicationPidFileWriter())
    }
}
