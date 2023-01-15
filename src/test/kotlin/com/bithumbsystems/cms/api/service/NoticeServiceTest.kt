package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.api.util.RedisKey.REDIS_NOTICE_FIX_KEY
import com.bithumbsystems.cms.persistence.mongo.entity.CmsFileInfo
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNoticeCategory
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisNotice
import com.fasterxml.jackson.core.type.TypeReference
import io.mockk.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
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
import kotlin.test.assertEquals

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
        val noticeList = CmsNotice(
            categoryIds = listOf("1"),
            title = "test board",
            content = "contents blah",
            shareTitle = "",
            shareDescription = "",
            createAccountId = "account_id",
            createDate = LocalDateTime.now(),
            screenDate = LocalDateTime.now()
        )
        val noticeTop = RedisNotice(
            id = "1",
            title = "title",
            createDate = LocalDateTime.now(),
            categoryNames = listOf("안내")
        )
        val typeReference = object : TypeReference<List<RedisNotice>>() {}

        coEvery { redisOperator.getTopList(REDIS_NOTICE_FIX_KEY, typeReference) } returns listOf(noticeTop)
        coEvery { cmsNoticeRepository.findCmsNoticeSearchTextAndPaging("", "", PageRequest.of(0, 15)) } returns flowOf(noticeList)
        coEvery { cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc() } returns flowOf(noticeList)
        coEvery { cmsNoticeRepository.countCmsNoticeSearchTextAndPaging("", "") } returns 1

        val result = noticeService.getNoticeList(BoardRequest("", "", 0, 15))

        verify { cmsNoticeRepository.findCmsNoticeSearchTextAndPaging("", "", PageRequest.of(0, 15)) }

        assertEquals(flowOf(noticeList.toResponse()).toList(), result.component1()!!.list.content)

        result.component1()?.list?.content `should be equal to` flowOf(noticeList.toResponse()).toList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNotice(): Unit = runTest {
        val topCmpNotice = CmsNotice(
            id = "notice_id",
            categoryIds = listOf("category_id"),
            title = "test board",
            content = "contents blah",
            shareTitle = "",
            shareDescription = "",
            createAccountId = "account_id",
            createDate = LocalDateTime.now(),
            screenDate = LocalDateTime.now(),
            fileId = "1"
        )

        val category = CmsNoticeCategory(
            id = "category_id",
            name = "공지사항",
            createAccountId = "account_id"
        )

        val fileInfo = CmsFileInfo(
            id = "file_id",
            size = 100,
            name = "file",
            extension = "jpg",
            createDate = LocalDateTime.now(),
            createAccountId = "account_id",
            createAccountEmail = "a@a.com"
        )

        coEvery { cmsNoticeRepository.findByIdAndIsShowAndIsDraftAndIsDelete(id = "notice_id") } returns topCmpNotice

        coEvery { cmsNoticeCategoryRepository.findAllById(listOf("category_id")) } returns flowOf(category)

        coEvery { cmsFileInfoRepository.findById("file_id") } returns fileInfo

        val result = noticeService.getNotice("notice_id")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getNoticeCategoryList(): Unit = runTest {

        val category = CmsNoticeCategory(
            id = "1",
            name = "공지사항",
            createAccountId = "account_id"
        )

        coEvery { cmsNoticeCategoryRepository.findAll() } returns flowOf(category)

        val result = noticeService.getNoticeCategoryList()

        coVerify { cmsNoticeCategoryRepository.findAll() }

        assertEquals(result.component1()?.first()?.id, "1")
    }
}
