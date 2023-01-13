package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEconomicResearch
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsEconomicResearchRepository : CoroutineSortingRepository<CmsEconomicResearch, String>, CmsEconomicResearchRepositoryCustom {
    fun findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc(
        isFixTop: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsEconomicResearch>
}

interface CmsEconomicResearchRepositoryCustom {
    fun findCmsEconomicResearchSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEconomicResearch>

    suspend fun countCmsEconomicResearchSearchTextAndPaging(searchText: String?): Long
}
