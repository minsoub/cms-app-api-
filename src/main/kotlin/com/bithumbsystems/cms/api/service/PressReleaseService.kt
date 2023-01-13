package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey.REDIS_PRESS_RELEASE_READ_COUNT_KEY
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsPressReleaseRepository
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
class PressReleaseService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsPressReleaseRepository: CmsPressReleaseRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {

    private val redisKey: String = RedisKey.REDIS_PRESS_RELEASE_FIX_KEY

    suspend fun getPressReleaseList(
        boardRequest: BoardRequest
    ): Result<DataResponse<BoardResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()
                val typeReference = object : TypeReference<List<RedisBoard>>() {}

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsPressReleaseList = cmsPressReleaseRepository.findCmsPressReleaseSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsPressReleaseList,
                        pageable,
                        cmsPressReleaseRepository.countCmsPressReleaseSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()
                val topList = cmsPressReleaseRepository.findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsPressReleaseList = cmsPressReleaseRepository.findCmsPressReleaseSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsPressReleaseList,
                        pageable,
                        cmsPressReleaseRepository.countCmsPressReleaseSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = { dataResponse ->
                val redisPressReleaseFix = dataResponse.fix.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisPressReleaseFix, RedisBoard::class.java)
            }
        )

    suspend fun getPressRelease(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsPressRelease = cmsPressReleaseRepository.findById(id)

                val boardDetailResponse: BoardDetailResponse? = cmsPressRelease?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                redisOperator.publish(redisKey = REDIS_PRESS_RELEASE_READ_COUNT_KEY, id = id)
            }
        )
}
