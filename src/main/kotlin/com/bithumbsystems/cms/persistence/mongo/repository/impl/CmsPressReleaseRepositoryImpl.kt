package com.bithumbsystems.cms.persistence.mongo.repository.impl

import com.bithumbsystems.cms.persistence.mongo.entity.CmsPressRelease
import com.bithumbsystems.cms.persistence.mongo.repository.CmsPressReleaseRepositoryCustom
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

    override fun findCmsPressReleaseSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsPressRelease> {
        return mongoTemplate.find(getCmsPressReleaseSearchTextAndPaging(searchText).with(pageable), CmsPressRelease::class.java).asFlow()
    }

    override fun countCmsPressReleaseSearchTextAndPaging(searchText: String?): Long {
        return mongoTemplate.count(getCmsPressReleaseSearchTextAndPaging(searchText), CmsPressRelease::class.java).block()!!
    }

    fun getCmsPressReleaseSearchTextAndPaging(searchText: String?): Query {
        val query = Query()
        val criteria = Criteria()
        val andOperator = mutableListOf<Criteria>()

        searchText?.let {
            criteria.orOperator(
                Criteria.where("title").regex(".*$searchText*.", "i"),
                Criteria.where("content").regex(".*$searchText*.", "i")
            )
        }

        andOperator.add(Criteria.where("is_show").`is`(true))
        andOperator.add(Criteria.where("is_delete").`is`(false))
        andOperator.add(Criteria.where("is_draft").`is`(false))
        andOperator.add(Criteria.where("is_fix_top").`is`(false))

        criteria.andOperator(
            andOperator
        )

        query.addCriteria(criteria)
        query.with(Sort.by(Sort.Direction.DESC, "screen_date"))

        return query
    }
}
