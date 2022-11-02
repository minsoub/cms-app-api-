package com.bithumbsystems.cms.integration

import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.persistence.mongo.enums.ResponseCode
import org.amshove.kluent.`should be`
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.properties.Delegates

@SpringBootTest
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CmsAppApiIntegrationTests(@Autowired val client: WebTestClient) {

    companion object {
        private var page by Delegates.notNull<Int>()
        private var size by Delegates.notNull<Int>()

        @BeforeAll
        @JvmStatic
        fun setUp() {
            page = 1
            size = 10
        }
    }

    @Rollback(false)
    @Order(1)
    @Test
    fun `get boards test`() {
        val responseBody: Response<*>? = client.get()
            .uri("/boards")
            .exchange()
            .expectStatus().isOk
            .expectBody(Response::class.java)
            .returnResult().responseBody

        println(responseBody)
        responseBody?.result `should be` ResponseCode.SUCCESS
    }
}
