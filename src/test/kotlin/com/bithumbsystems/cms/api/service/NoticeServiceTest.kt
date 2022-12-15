package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeAll

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class NoticeServiceTest {

    private lateinit var ioDispatcher: CoroutineDispatcher
    private lateinit var cmsNoticeRepository: CmsNoticeRepository
    private lateinit var cmsNoticeCategoryRepository: CmsNoticeCategoryRepository
    private lateinit var cmsFileInfoRepository: CmsFileInfoRepository
    private lateinit var redisOperator: RedisOperator
    private lateinit var noticeService: NoticeService

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
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNoticeList(): Unit = runTest {
        val topCmpNotice = CmsNotice(
            categoryId = listOf("1"),
            title = "test board",
            content = "contents blah",
            shareTitle = "",
            shareDescription = "",
            createAccountId = "account_id",
            createDate = LocalDateTime.now(),
            screenDate = LocalDateTime.now()
        )

        coEvery { cmsNoticeRepository.findCmsNoticeSearchTextAndPaging("", "", PageRequest.of(0, 15)) } returns flowOf(topCmpNotice)
        coEvery { cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc() } returns flowOf(topCmpNotice)
        coEvery { cmsNoticeRepository.countCmsNoticeSearchTextAndPaging("", "") } returns 1

        var result = noticeService.getNoticeList("", "", 0, 15)

        println(result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNotice(): Unit = runTest {
        val topCmpNotice = CmsNotice(
            categoryId = listOf("1"),
            title = "test board",
            content = "contents blah",
            shareTitle = "",
            shareDescription = "",
            createAccountId = "account_id",
            createDate = LocalDateTime.now(),
            screenDate = LocalDateTime.now()
        )

        coEvery { cmsNoticeRepository.findById("1") } returns topCmpNotice

        val result = noticeService.getNotice("1")

        result.component1()?.title `should be equal to` null
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNoticeCategoryList(): Unit = runTest {
        var result = noticeService.getNoticeCategoryList()
    }
}
