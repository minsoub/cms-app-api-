package com.bithumbsystems.cms.persistence.mongo.repository.impl

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEconomicResearch
import com.bithumbsystems.cms.persistence.mongo.repository.CmsEconomicResearchRepositoryCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CmsEconomicResearchRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : CmsEconomicResearchRepositoryCustom {

    override fun findCmsEconomicResearchSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEconomicResearch> {
        return mongoTemplate.find(getCmsPressReleaseSearchTextAndPaging(searchText).with(pageable), CmsEconomicResearch::class.java).asFlow()
    }

    override suspend fun countCmsEconomicResearchSearchTextAndPaging(searchText: String?): Long {
        return mongoTemplate.count(getCmsPressReleaseSearchTextAndPaging(searchText), CmsEconomicResearch::class.java).awaitSingle()
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
