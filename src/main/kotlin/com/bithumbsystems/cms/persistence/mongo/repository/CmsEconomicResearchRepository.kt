package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEconomicResearch
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CmsEconomicResearchRepository : CoroutineSortingRepository<CmsEconomicResearch, String>, CmsEconomicResearchRepositoryCustom {
    fun findCmsEconomicResearchByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsEconomicResearch>
}

interface CmsEconomicResearchRepositoryCustom {
    fun findCmsEconomicResearchSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEconomicResearch>

    fun countCmsEconomicResearchSearchTextAndPaging(searchText: String?): Mono<Long>
}
