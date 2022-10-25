package com.bithumbsystems.cms.api.service.operator

import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Service

@Service
class RedisOperator(
    private val redissonReactiveClient: RedissonReactiveClient
)
