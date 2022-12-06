package com.bithumbsystems.cms.persistence.mongo.repository

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CmsNoticeRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : CmsNoticeRepositoryCustom {

    override fun findCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?, pageNo: Int, pageSize: Int): Flow<CmsNotice> {
        val query = Query()
        val criteria = Criteria()

        searchText?.let {
            criteria.orOperator(
                Criteria.where("title").regex(".*$searchText*.", "i"),
                Criteria.where("content").regex(".*$searchText*.", "i")
            )
        }

        categoryId?.let {
            query.addCriteria(
                Criteria.where("category_id").`in`(categoryId)
            )
        }

        criteria.andOperator(
            Criteria.where("is_show").`is`(true)
        )

        val pageable = PageRequest.of(pageNo, pageSize)

        query.addCriteria(criteria)
        query.with(Sort.by(Sort.Direction.DESC, "screen_date"))
        query.with(pageable)

        return mongoTemplate.find(query, CmsNotice::class.java).asFlow()
    }
}
