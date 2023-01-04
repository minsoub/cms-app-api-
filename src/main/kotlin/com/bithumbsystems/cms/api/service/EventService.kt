package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.mongo.repository.CmsEventRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisBoard
import com.bithumbsystems.cms.persistence.redis.model.toRedis
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class EventService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsEventRepository: CmsEventRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator,
) {

    private val redisKey: String = RedisKey.REDIS_EVENT_FIX_KEY

    suspend fun getEventList(
        boardRequest: BoardRequest
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey, RedisBoard::class.java).map { it.toResponse() }

                val cmsEventList = cmsEventRepository.findCmsEventSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEventRepository.countCmsEventSearchTextAndPaging(boardRequest.searchText).awaitSingle()
                    )
                )
            },
            fallback = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList = cmsEventRepository.findCmsEventByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsEventList = cmsEventRepository.findCmsEventSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEventRepository.countCmsEventSearchTextAndPaging(boardRequest.searchText).awaitSingle()
                    )
                )
            },
            afterJob = {
                val fixList = cmsEventRepository.findCmsEventByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val redisEventFix = fixList.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisEventFix, RedisBoard::class.java)
            }
        )

    suspend fun getEvent(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsPressRelease = cmsEventRepository.findById(id)

                val boardDetailResponse: BoardDetailResponse? = cmsPressRelease?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                val cmsPressRelease = cmsEventRepository.findById(id)

                cmsPressRelease?.let {
                    // redis 조회 수
                }
            }
        )
}
