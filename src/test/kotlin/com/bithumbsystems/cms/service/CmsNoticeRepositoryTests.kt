package com.bithumbsystems.cms.service

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.`should be equal to`
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.time.ZoneOffset

@DataJpaTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
class CmsNoticeRepositoryTests(
    @Autowired
    private val cmsNoticeRepository: CmsNoticeRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `get cmsNotice test`() = runTest {
        val cmsNotice = CmsNotice(
            categoryId = "NOTICE",
            title = "test board",
            content = "contents blah",
            shareTitle = "",
            shareDescription = "",
            createAccountId = "you",
            createDate = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        )

        cmsNoticeRepository.save(cmsNotice).awaitSingle()
        val savedCmsNotice = cmsNoticeRepository.findAll().awaitFirst()

        println(savedCmsNotice)
        cmsNotice `should be equal to` savedCmsNotice.content
    }
}
