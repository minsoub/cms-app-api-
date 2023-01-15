package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsNoticeRepository : CoroutineSortingRepository<CmsNotice, String>, CmsNoticeRepositoryCustom {
    fun findCmsNoticeByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc(
        isFixTop: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsNotice>

    fun findCmsNoticeByIsBannerAndIsShowAndIsDraftAndIsDelete(
        isBanner: Boolean = true,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): Flow<CmsNotice>

    suspend fun findByIdAndIsShowAndIsDraftAndIsDelete(
        id: String,
        isShow: Boolean = true,
        isDraft: Boolean = false,
        isDelete: Boolean = false
    ): CmsNotice?
}

interface CmsNoticeRepositoryCustom {
    fun findCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?, pageable: PageRequest): Flow<CmsNotice>

    suspend fun countCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?): Long

    fun findCmsNoticePaging(pageable: PageRequest): Flow<CmsNotice>
}
