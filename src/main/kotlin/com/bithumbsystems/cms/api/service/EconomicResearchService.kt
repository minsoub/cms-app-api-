package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey.REDIS_ECONOMIC_RESEARCH_READ_COUNT_KEY
import com.bithumbsystems.cms.persistence.mongo.repository.CmsEconomicResearchRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisBoard
import com.bithumbsystems.cms.persistence.redis.model.RedisThumbnail
import com.bithumbsystems.cms.persistence.redis.model.toRedis
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class EconomicResearchService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsEconomicResearchRepository: CmsEconomicResearchRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {

    private val redisKey: String = RedisKey.REDIS_ECONOMIC_RESEARCH_FIX_KEY

    suspend fun getEconomicResearchList(
        boardRequest: BoardRequest
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisThumbnail>>() {}

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsEventList = cmsEconomicResearchRepository.findCmsEconomicResearchSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEconomicResearchRepository.countCmsEconomicResearchSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()

                val topList = cmsEconomicResearchRepository.findCmsEconomicResearchByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsEventList = cmsEconomicResearchRepository.findCmsEconomicResearchSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsEventList,
                        pageable,
                        cmsEconomicResearchRepository.countCmsEconomicResearchSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = {
                val fixList = cmsEconomicResearchRepository.findCmsEconomicResearchByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val redisEventFix = fixList.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisEventFix, RedisBoard::class.java)
            }
        )

    suspend fun getEconomicResearch(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsEconomicResearch = cmsEconomicResearchRepository.findById(id)

                val boardDetailResponse: BoardDetailResponse? = cmsEconomicResearch?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                redisOperator.publish(redisKey = REDIS_ECONOMIC_RESEARCH_READ_COUNT_KEY, id = id)
            }
        )
}
