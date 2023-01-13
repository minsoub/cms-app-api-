package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsReviewReport
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsReviewReportRepository : CoroutineSortingRepository<CmsReviewReport, String>, CmsReviewReportRepositoryCustom {
    fun findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc(
        isFixTop: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsReviewReport>
}

interface CmsReviewReportRepositoryCustom {
    fun findCmsReviewReportSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsReviewReport>

    suspend fun countCmsReviewReportSearchTextAndPaging(searchText: String?): Long
}
