package com.bithumbsystems.cms.api.config.filter

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.CONTEXT_NAME
import com.bithumbsystems.cms.api.util.Logger
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.util.context.Context
import java.util.*

@Component
class RequestFilter : WebFilter {

    private val logger by Logger()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestId = Mono.fromCallable {
            UUID.randomUUID().toString()
        }.subscribeOn(Schedulers.boundedElastic())

        return requestId.flatMap { requestId ->
            chain.filter(exchange).contextWrite {
                logger.debug("requestId: $requestId")
                Context.of(CONTEXT_NAME, requestId)
            }
        }
    }
}
