package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CmsPressReleaseRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : CmsPressReleaseRepositoryCustom {

    override fun findCmsPressReleaseSearchTextAndPaging(searchText: String?, pageNo: Int, pageSize: Int): Flow<CmsPressRelease> {
        val query = Query()
        val criteria = Criteria()

        searchText?.let {
            criteria.orOperator(
                Criteria.where("title").regex(".*$searchText*.", "i"),
                Criteria.where("content").regex(".*$searchText*.", "i")
            )
        }

        criteria.andOperator(
            Criteria.where("is_show").`is`(true)
        )

        val pageable = PageRequest.of(pageNo, pageSize)

        query.addCriteria(criteria)
        query.with(Sort.by(Sort.Direction.DESC, "screen_date"))
        query.with(pageable)

        return mongoTemplate.find(query, CmsPressRelease::class.java).asFlow()
    }
}
