package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsPressReleaseRepository : CoroutineSortingRepository<CmsPressRelease, String>, CmsPressReleaseRepositoryCustom {
    fun findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc(
        isFixTop: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsPressRelease>

    suspend fun findByIdAndIsShowAndIsDraftAndIsDelete(
        id: String,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): CmsPressRelease?
}

interface CmsPressReleaseRepositoryCustom {
    fun findCmsPressReleaseSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsPressRelease>

    suspend fun countCmsPressReleaseSearchTextAndPaging(searchText: String?): Long

    fun findCmsPressReleasePaging(pageable: PageRequest): Flow<CmsPressRelease>
}
