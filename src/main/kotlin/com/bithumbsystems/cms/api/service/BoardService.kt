package com.bithumbsystems.cms.api.service

import arrow.core.Either
import com.bithumbsystems.cms.api.model.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.api.service.operator.ServiceOperator.execute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service

@Service
class BoardService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsNoticeRepository: CmsNoticeRepository
) {

    suspend fun getOne(id: String): BoardResponse? = execute(
        dispatcher = ioDispatcher,
        validator = {
            cmsNoticeRepository.existsById(id).awaitSingle()
        },
        job = {
            cmsNoticeRepository.findById(id).awaitSingleOrNull()?.toResponse()
        },
        afterJob = {
        },
        fallback = {
        }
    )

    suspend fun getOneEither(id: String): Either<Exception, BoardResponse?> {
        return TODO("Provide the return value")
    }

    suspend fun getList(): List<BoardResponse>? = execute(
        dispatcher = ioDispatcher,
        validator = null,
        job = {
            cmsNoticeRepository.findAll().asFlow().map {
                it.toResponse()
            }.toList()
        },
        afterJob = {
        },
        fallback = {
        }
    )
}
