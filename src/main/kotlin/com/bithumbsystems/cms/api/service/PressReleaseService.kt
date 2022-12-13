package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsPressReleaseRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service

@Service
class PressReleaseService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsPressReleaseRepository: CmsPressReleaseRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {
    suspend fun getPressReleaseList(
        searchText: String?,
        pageNo: Int,
        pageSize: Int
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val topList = redisOperator.getTopNotice().map {
                    it.toResponse()
                }.toList()

                DataResponse(topList, Page.empty())
            },
            fallback = {
                val cmsNoticeTopList = cmsPressReleaseRepository.findCmsPressReleaseByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                DataResponse(cmsNoticeTopList, Page.empty())
            },
            afterJob = {
            }
        )

    suspend fun getPressRelease(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            action = {
                val cmsPressRelease = cmsPressReleaseRepository.findById(id)

                val boardDetailResponse: BoardDetailResponse? = cmsPressRelease?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            }
        )
}
