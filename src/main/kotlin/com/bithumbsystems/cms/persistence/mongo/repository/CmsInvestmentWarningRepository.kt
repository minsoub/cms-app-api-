package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsInvestmentWarning
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CmsInvestmentWarningRepository : CoroutineSortingRepository<CmsInvestmentWarning, String> {
    fun findFirstByIsShowAndIsDeleteAndIsDraftOrderByScreenDateDesc(
        isShow: Boolean = true,
        isDelete: Boolean = false,
        isDraft: Boolean = false
    ): Mono<CmsInvestmentWarning>
}
