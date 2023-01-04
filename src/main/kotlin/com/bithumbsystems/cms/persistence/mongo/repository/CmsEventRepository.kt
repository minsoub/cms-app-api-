package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEvent
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CmsEventRepository : CoroutineSortingRepository<CmsEvent, String>, CmsEventRepositoryCustom {
    fun findCmsEventByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsEvent>
}

interface CmsEventRepositoryCustom {
    fun findCmsEventSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEvent>

    fun countCmsEventSearchTextAndPaging(searchText: String?): Mono<Long>
}
