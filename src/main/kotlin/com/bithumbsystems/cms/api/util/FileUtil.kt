package com.bithumbsystems.cms.api.util

import com.bithumbsystems.cms.api.config.aws.AwsProperties
import org.springframework.stereotype.Component

@Component
private class KotlinAwsProperties(
    awsProperties: AwsProperties
) {
    init {
        KotlinAwsProperties.awsProperties = awsProperties
    }

    companion object {
        lateinit var awsProperties: AwsProperties
    }
}

fun String.getS3Url(): String =
    KotlinAwsProperties.awsProperties.let {
        "https://${it.bucket}.s3.${it.region}.amazonaws.com/$this"
    }
