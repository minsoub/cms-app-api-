package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEvent
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsEventRepository : CoroutineSortingRepository<CmsEvent, String>, CmsEventRepositoryCustom {
    fun findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc(
        isFixTop: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsEvent>
}

interface CmsEventRepositoryCustom {
    fun findCmsEventSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEvent>

    suspend fun countCmsEventSearchTextAndPaging(searchText: String?): Long
}
