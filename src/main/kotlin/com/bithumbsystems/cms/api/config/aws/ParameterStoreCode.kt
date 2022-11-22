package com.bithumbsystems.cms.api.config.aws

enum class ParameterStoreCode(val value: String) {
    DB_URL("dburl"),
    DB_USER("user"),
    DB_PORT("port"),
    DB_NAME("dbname"),
    DB_PASSWORD("passwd"),
    REDIS_HOST("host"),
    REDIS_PORT("port"),
    REDIS_TOKEN("token"),
    KMS_ALIAS_NAME("key"),
    CRYPTO_KEY("key")
}
