package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNoticeCategory
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface CmsNoticeCategoryRepository : CoroutineSortingRepository<CmsNoticeCategory, String> {
    fun findByIsUseTrueAndIsDeleteFalse(): Flow<CmsNoticeCategory>
}
