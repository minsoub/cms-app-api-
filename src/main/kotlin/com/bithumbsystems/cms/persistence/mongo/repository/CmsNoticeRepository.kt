package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsNoticeRepository : CoroutineSortingRepository<CmsNotice, String>, CmsNoticeRepositoryCustom {
    fun findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc(isFixTop: Boolean = true, isShow: Boolean = true): Flow<CmsNotice>
}

interface CmsNoticeRepositoryCustom {
    fun findCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?, pageNo: Int, pageSize: Int): Flow<CmsNotice>
}
