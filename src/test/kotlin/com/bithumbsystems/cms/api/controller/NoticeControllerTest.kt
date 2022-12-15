package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.service.NoticeService
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.CoroutineDispatcher
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SpringExtension::class)
class NoticeControllerTest @Autowired constructor(
    private val client: WebTestClient
) {

    private lateinit var noticeService: NoticeService
    private lateinit var noticeController: NoticeController
    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var cmsNoticeRepository: CmsNoticeRepository
    private lateinit var cmsNoticeCategoryRepository: CmsNoticeCategoryRepository
    private lateinit var cmsFileInfoRepository: CmsFileInfoRepository
    private lateinit var redisOperator: RedisOperator

    @BeforeAll
    fun beforeAll() {
        ioDispatcher = mockk(relaxed = true)
        cmsNoticeRepository = mockk(relaxed = true)
        cmsNoticeCategoryRepository = mockk(relaxed = true)
        cmsFileInfoRepository = mockk(relaxed = true)
        redisOperator = mockk(relaxed = true)
        noticeService = spyk(
            objToCopy = NoticeService(ioDispatcher, cmsNoticeRepository, cmsNoticeCategoryRepository, cmsFileInfoRepository, redisOperator),
            recordPrivateCalls = true
        )
//        noticeService = mockk(relaxed = true)
        noticeController = spyk(
            objToCopy = NoticeController(noticeService),
            recordPrivateCalls = true
        )
    }

    @Test
    fun noticeList() {
//        val boardRequest: BoardRequest = BoardRequest(pageNo = 0, pageSize = 15, categoryId = "", searchText = "")
//
//        val topCmpNotice = CmsNotice(
//            categoryId = listOf("1"),
//            title = "test board",
//            content = "contents blah",
//            shareTitle = "",
//            shareDescription = "",
//            createAccountId = "account_id",
//            createDate = LocalDateTime.now(),
//            screenDate = LocalDateTime.now()
//        )
//
//        coEvery { noticeRepository.findCmsNoticeSearchTextAndPaging("", "", PageRequest.of(0, 15)) } returns flowOf(topCmpNotice)
//        coEvery { noticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc()} returns flowOf(topCmpNotice)
//        coEvery { noticeRepository.countCmsNoticeSearchTextAndPaging("", "")} returns 1

//        val result: Result<DataResponse?, ErrorData> = binding { DataResponse(listOf(), Page.empty()) }

//        coEvery { noticeService.getNoticeList("", "", 0, 15) } returns result

        client
            .get().uri("/api/v1/cms/notice/list?category_id=1&page_no=0&page_size=15")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java).isEqualTo("Hello World")

//        val response = client.get()
//            .uri {builder -> builder.path("/api/v1/cms/notice/list")
//                .queryParam("page_no", boardRequest.pageNo)
//                .queryParam("page_size", boardRequest.pageSize)
//                .queryParam("category_id", boardRequest.categoryId)
//                .queryParam("search_text", boardRequest.searchText)
//                .build()}
//            .exchange()
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
    }

    @Test
    fun noticeCategory() {
        client.get()
            .uri { builder ->
                builder
                    .path("/api/v1/cms/category")
                    .build()
            }
            .exchange()
    }
}
