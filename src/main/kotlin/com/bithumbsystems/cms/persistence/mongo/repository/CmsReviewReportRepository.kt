package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsReviewReport
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsReviewReportRepository : CoroutineSortingRepository<CmsReviewReport, String>, CmsReviewReportRepositoryCustom {
    fun findCmsReviewReportByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsReviewReport>
}

interface CmsReviewReportRepositoryCustom {
    fun findCmsReviewReportSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsReviewReport>

    fun countCmsReviewReportSearchTextAndPaging(searchText: String?): Long
}
