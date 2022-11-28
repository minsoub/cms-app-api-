package com.bithumbsystems.cms.api.config.client.impl

import com.bithumbsystems.cms.api.config.aws.AwsProperties
import com.bithumbsystems.cms.api.config.aws.ParameterStoreConfig
import com.bithumbsystems.cms.api.config.client.ClientBuilder
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.redisson.Redisson
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.stereotype.Component
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.kms.KmsAsyncClient
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.ssm.SsmClient
import java.net.URI

@Component
@Profile(value = ["dev", "qa", "prod", "eks-dev"])
class ClientBuilderImpl : ClientBuilder {
    override fun buildSsm(awsProperties: AwsProperties): SsmClient =
        SsmClient.builder().endpointOverride(URI.create(awsProperties.ssmEndPoint))
            .region(Region.of(awsProperties.region)).build()

    @Bean
    override fun buildS3(awsProperties: AwsProperties): S3AsyncClient =
        S3AsyncClient.builder().region(Region.of(awsProperties.region)).build()

    override fun buildKms(awsProperties: AwsProperties): KmsAsyncClient =
        KmsAsyncClient.builder().region(Region.of(awsProperties.region))
            .endpointOverride(URI.create(awsProperties.kmsEndPoint)).build()

    override fun buildMongo(mongoClientSettings: MongoClientSettings): MongoClient =
        MongoClients.create(mongoClientSettings)

    @Bean
    fun redissonReactiveClient(parameterStoreConfig: ParameterStoreConfig): RedissonReactiveClient {
        val config = Config()
        val redisPort = parameterStoreConfig.redisProperties.port
        config.useReplicatedServers().nodeAddresses = listOf("redis://${parameterStoreConfig.redisProperties.host}:$redisPort")
        if (!parameterStoreConfig.redisProperties.token.isNullOrEmpty()) {
            config.useReplicatedServers().password = RedisPassword.of(parameterStoreConfig.redisProperties.token).toString()
        }

        return Redisson.create(config).reactive()
    }
}
