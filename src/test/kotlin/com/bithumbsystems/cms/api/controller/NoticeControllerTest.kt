package com.bithumbsystems.cms.api.controller

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class NoticeControllerTest @Autowired constructor(
    private val client: WebTestClient
) {

    @Test
    fun noticeList() {
        client.get()
            .uri { builder ->
                builder
                    .path("/api/v1/cms/notice/list")
                    .queryParam("page_no", "0")
                    .queryParam("page_size", "15")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun noticeDetail() {
        client.get()
            .uri { builder ->
                builder
                    .path("/api/v1/cms/notice/detail/1")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
    }

    @Test
    fun noticeCategory() {
        client.get()
            .uri { builder ->
                builder
                    .path("/api/v1/cms/notice/category")
                    .build()
            }
            .exchange()
            .expectStatus().isOk
    }
}
