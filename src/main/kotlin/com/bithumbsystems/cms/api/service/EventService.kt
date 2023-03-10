package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey.REDIS_EVENT_READ_COUNT_KEY
import com.bithumbsystems.cms.persistence.mongo.repository.CmsEventRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisBoard
import com.bithumbsystems.cms.persistence.redis.model.toRedis
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class EventService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsEventRepository: CmsEventRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {

    private val redisKey: String = RedisKey.REDIS_EVENT_FIX_KEY

    suspend fun getEventList(
        boardRequest: BoardRequest
    ): Result<DataResponse<BoardResponse, BoardResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisBoard>>() {}

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsEventList = cmsEventRepository.findCmsEventSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEventRepository.countCmsEventSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()

                val topList = cmsEventRepository.findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsEventList = cmsEventRepository.findCmsEventSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEventRepository.countCmsEventSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = { dataResponse ->
                val redisEventFix = dataResponse.fix.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisEventFix, RedisBoard::class.java)
            }
        )

    suspend fun getEvent(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsEvent = cmsEventRepository.findByIdAndIsShowAndIsDraftAndIsDelete(id = id)

                val boardDetailResponse: BoardDetailResponse? = cmsEvent?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                redisOperator.publish(redisKey = REDIS_EVENT_READ_COUNT_KEY, id = id)
            }
        )
}
