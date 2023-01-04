package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsReviewReportRepository
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
class ReviewReportService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsReviewReportRepository: CmsReviewReportRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator,
) {

    private val redisKey: String = RedisKey.REDIS_REVIEW_REPORT_FIX_KEY

    suspend fun getReviewReportList(
        boardRequest: BoardRequest
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey).map { it.toResponse() }

                val cmsReviewReport = cmsReviewReportRepository.findCmsReviewReportSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsReviewReport,
                        pageable,
                        cmsReviewReportRepository.countCmsReviewReportSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList = cmsReviewReportRepository.findCmsReviewReportByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsReviewReport = cmsReviewReportRepository.findCmsReviewReportSearchTextAndPaging(boardRequest.searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsReviewReport,
                        pageable,
                        cmsReviewReportRepository.countCmsReviewReportSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = {
                val fixList = cmsReviewReportRepository.findCmsReviewReportByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val redisNoticeFix = fixList.map {
                    it.toNoticeFix()
                }

                redisOperator.setTopList(redisKey, redisNoticeFix)
            }
        )

    suspend fun getReviewReport(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsReviewReport = cmsReviewReportRepository.findById(id)

                val boardDetailResponse: BoardDetailResponse? = cmsReviewReport?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                val cmscmsReviewReport = cmsReviewReportRepository.findById(id)

                cmscmsReviewReport?.let {
                    // redis 조회 수
                }
            }
        )
}
