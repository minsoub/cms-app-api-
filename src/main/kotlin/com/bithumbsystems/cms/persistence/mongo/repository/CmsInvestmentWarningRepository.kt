package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsInvestmentWarning
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsInvestmentWarningRepository : CoroutineSortingRepository<CmsInvestmentWarning, String> {
    suspend fun findFirstByIsShowAndIsDeleteAndIsDraftOrderByScreenDateDesc(
        isShow: Boolean = true,
        isDelete: Boolean = false,
        isDraft: Boolean = false
    ): CmsInvestmentWarning?
}
