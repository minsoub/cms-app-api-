package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toEntity
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val ioDispatcher: CoroutineDispatcher,
    private val redisOperator: RedisOperator,
    private val cmsNoticeRepository: CmsNoticeRepository
) {

    suspend fun getOne(id: String): Result<BoardResponse?, ErrorData> =
        executeIn(
            validator = { cmsNoticeRepository.existsById(id) },
            action = {
                val cms = cmsNoticeRepository.findById(id)
                cms?.toResponse()
            }
        )

    suspend fun getList(): Result<List<BoardResponse>?, ErrorData> =
        executeIn(
            action = {
                cmsNoticeRepository.findAll().map {
                    it.toResponse()
                }.toList()
            }
        )

    suspend fun getTop(): Result<BoardResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                redisOperator.getTopNotice()?.toResponse()
            },
            fallback = {
                cmsNoticeRepository.findById("1")?.toResponse()
            },
            afterJob = {
                redisOperator.setTopNotice(it.toEntity())
            }
        )
}
