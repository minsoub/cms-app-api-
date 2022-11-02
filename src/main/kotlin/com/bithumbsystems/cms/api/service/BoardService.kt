package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.api.service.operator.ServiceOperator.Companion.executeIn
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val cmsNoticeRepository: CmsNoticeRepository
) {

    suspend fun getOne(id: String): Result<BoardResponse?, ErrorData> =
        executeIn(
            validator = { cmsNoticeRepository.existsById(id).awaitSingle() },
            action = {
                val cms = cmsNoticeRepository.findById(id).awaitSingleOrNull()
                cms?.toResponse()
            }
        )

    suspend fun getList(): Result<List<BoardResponse>?, ErrorData> =
        executeIn(
            action = {
                cmsNoticeRepository.findAll().asFlow().map {
                    it.toResponse()
                }.toList()
            }
        )
}
