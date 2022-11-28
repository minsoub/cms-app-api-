//package com.bithumbsystems.cms.service
//
//import com.bithumbsystems.cms.api.service.BoardService
//import com.bithumbsystems.cms.persistence.redis.RedisOperator
//import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
//import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
//import io.mockk.coEvery
//import io.mockk.mockk
//import io.mockk.spyk
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.amshove.kluent.`should be equal to`
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import java.time.LocalDateTime
//
//class BoardServiceTests {
//
//    private lateinit var boardService: BoardService
//    private lateinit var redisOperator: RedisOperator
//    private lateinit var cmsNoticeRepository: CmsNoticeRepository
//
//    @BeforeEach
//    fun setUp() {
//        redisOperator = mockk()
//        cmsNoticeRepository = mockk()
//
//        boardService = spyk(
//            objToCopy = BoardService(
//                Dispatchers.IO,
//                redisOperator,
//                cmsNoticeRepository
//            ),
//            recordPrivateCalls = true
//        )
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `redis 에서 top cmsNotice 성공적으로 가져오기`() = runTest {
//        val topCmpNotice = CmsNotice(
//            categoryId = listOf("NOTICE"),
//            title = "test board",
//            content = "contents blah",
//            shareTitle = "",
//            shareDescription = "",
//            createAccountId = "you",
//            createDate = LocalDateTime.now(),
//            screenDate = LocalDateTime.now()
//        )
//
//        coEvery {
//            redisOperator.getTopNotice()
//        } returns topCmpNotice
//
//        coEvery {
//            cmsNoticeRepository.findById("1")
//        } returns topCmpNotice
//
//        val result = boardService.getTop()
//
//        result.component1()?.content `should be equal to` "contents blah"
//    }
//
//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun `mongo 에서 cmsNotice 성공적으로 가져오기`() = runTest {
//        val id = "1"
//        val topCmpNotice = CmsNotice(
//            categoryId = listOf("NOTICE"),
//            title = "test board",
//            content = "contents blah",
//            shareTitle = "",
//            shareDescription = "",
//            createAccountId = "you",
//            createDate = LocalDateTime.now(),
//            screenDate = LocalDateTime.now()
//        )
//
//        coEvery {
//            cmsNoticeRepository.existsById(id)
//        } returns true
//
//        coEvery {
//            cmsNoticeRepository.findById(id)
//        } returns topCmpNotice
//
//        val result = boardService.getOne(id)
//
//        result.component1()?.content `should be equal to` "contents blah"
//    }
//}
