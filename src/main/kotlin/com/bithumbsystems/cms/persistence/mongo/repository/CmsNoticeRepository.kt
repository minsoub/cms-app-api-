package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CmsNoticeRepository : CoroutineSortingRepository<CmsNotice, String>, CmsNoticeRepositoryCustom {
    fun findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsNotice>

    fun findCmsNoticeByIsBannerAndIsShow(isBanner: Boolean = true, isShow: Boolean = true): Flow<CmsNotice>
}

interface CmsNoticeRepositoryCustom {
    fun findCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?, pageable: PageRequest): Flow<CmsNotice>

    fun countCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?): Mono<Long>
}
