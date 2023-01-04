package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CmsPressReleaseRepository : CoroutineSortingRepository<CmsPressRelease, String>, CmsPressReleaseRepositoryCustom {
    fun findCmsPressReleaseByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsPressRelease>
}

interface CmsPressReleaseRepositoryCustom {
    fun findCmsPressReleaseSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsPressRelease>

    fun countCmsPressReleaseSearchTextAndPaging(searchText: String?): Mono<Long>
}
