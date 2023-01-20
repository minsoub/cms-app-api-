package com.bithumbsystems.cms.persistence.mongo.repository.impl

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepositoryCustom
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.TextCriteria
import org.springframework.stereotype.Repository

@Repository
class CmsNoticeRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate
) : CmsNoticeRepositoryCustom {

    override fun findCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?, pageable: PageRequest): Flow<CmsNotice> {
        return mongoTemplate.find(getCmsNoticeSearchTextAndPaging(categoryId, searchText).with(pageable), CmsNotice::class.java).asFlow()
    }

    override suspend fun countCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?): Long {
        return mongoTemplate.count(getCmsNoticeSearchTextAndPaging(categoryId, searchText), CmsNotice::class.java).awaitSingle()
    }

    override fun findCmsNoticeSearchTextAndPagingInTitleContent(categoryId: String?, searchText: String?, pageable: PageRequest): Flow<CmsNotice> {
        return mongoTemplate.find(
            getCmsNoticeSearchTextAndPagingInTitleContent(categoryId, searchText).with(pageable),
            CmsNotice::class.java
        ).asFlow()
    }

    override suspend fun countCmsNoticeSearchTextAndPagingInTitleContent(categoryId: String?, searchText: String?): Long {
        return mongoTemplate.count(getCmsNoticeSearchTextAndPagingInTitleContent(categoryId, searchText), CmsNotice::class.java).awaitSingle()
    }

    override fun findCmsNoticePaging(pageable: PageRequest): Flow<CmsNotice> {
        return mongoTemplate.find(getCmsNoticeSearchTextAndPaging(null, null).with(pageable), CmsNotice::class.java).asFlow()
    }

    fun getCmsNoticeSearchTextAndPagingInTitleContent(categoryId: String?, searchText: String?): Query {
        val query = Query()
        val criteria = Criteria()
        val andOperator = mutableListOf<Criteria>()

        searchText?.let {
            criteria.orOperator(
                Criteria.where("title").regex(".*$searchText*.", "i"),
                Criteria.where("content").regex(".*$searchText*.", "i")
            )
        }

        categoryId?.let {
            andOperator.add(Criteria.where("category_ids").`in`(categoryId))
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

    fun getCmsNoticeSearchTextAndPaging(categoryId: String?, searchText: String?): Query {
        val query = Query()
        val criteria = Criteria()
        val andOperator = mutableListOf<Criteria>()

        val textCriteria = TextCriteria()
        searchText?.let {
            textCriteria.matching(searchText)
            query.addCriteria(textCriteria)
        }

        categoryId?.let {
            andOperator.add(Criteria.where("category_ids").`in`(categoryId))
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
