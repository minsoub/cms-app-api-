package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsPressReleaseRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.toNoticeFix
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class PressReleaseService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsPressReleaseRepository: CmsPressReleaseRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator,
) {

    private val redisKey: String = RedisKey.REDIS_PRESS_RELEASE_FIX_KEY

    suspend fun getPressReleaseList(
        boardRequest: BoardRequest
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList: List<BoardResponse> = redisOperator.getTopNotice(redisKey).map { it.toResponse() }

                val cmsPressReleaseList = cmsPressReleaseRepository.findCmsNoticeSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsPressReleaseList,
                        pageable,
                        cmsPressReleaseRepository.countCmsNoticeSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList = cmsPressReleaseRepository.findCmsPressReleaseByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsPressReleaseList = cmsPressReleaseRepository.findCmsNoticeSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsPressReleaseList,
                        pageable,
                        cmsPressReleaseRepository.countCmsNoticeSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = {
                val fixList = cmsPressReleaseRepository.findCmsPressReleaseByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val redisNoticeFix = fixList.map {
                    it.toNoticeFix()
                }
                redisOperator.setTopNotice(redisKey, redisNoticeFix)
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
                val cmsPressRelease = cmsPressReleaseRepository.findById(id)

                cmsPressRelease?.let {
                    // redis 조회 수
                }
            }
        )
}
