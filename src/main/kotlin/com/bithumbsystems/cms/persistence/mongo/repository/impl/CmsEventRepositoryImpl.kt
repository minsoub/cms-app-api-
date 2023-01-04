package com.bithumbsystems.cms.persistence.mongo.repository.impl

import com.bithumbsystems.cms.persistence.mongo.entity.CmsEvent
import com.bithumbsystems.cms.persistence.mongo.repository.CmsEventRepositoryCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class CmsEventRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : CmsEventRepositoryCustom {

    override fun findCmsEventSearchTextAndPaging(searchText: String?, pageable: PageRequest): Flow<CmsEvent> {
        return mongoTemplate.find(getCmsPressReleaseSearchTextAndPaging(searchText).with(pageable), CmsEvent::class.java).asFlow()
    }

    override fun countCmsEventSearchTextAndPaging(searchText: String?): Mono<Long> {
        return mongoTemplate.count(getCmsPressReleaseSearchTextAndPaging(searchText), CmsEvent::class.java)
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
