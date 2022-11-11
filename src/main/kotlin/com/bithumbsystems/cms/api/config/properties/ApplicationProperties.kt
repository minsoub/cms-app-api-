package com.bithumbsystems.cms.api.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "application")
class ApplicationProperties(
    val version: String,
    val prefix: String,
    val excludePrefixPath: Array<String>,
    val siteId: String,
    val roleType: String
)
