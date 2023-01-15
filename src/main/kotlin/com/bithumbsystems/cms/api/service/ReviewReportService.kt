package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey.REDIS_REVIEW_REPORT_READ_COUNT_KEY
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsReviewReportRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
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
class ReviewReportService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsReviewReportRepository: CmsReviewReportRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {

    private val redisKey: String = RedisKey.REDIS_REVIEW_REPORT_FIX_KEY

    suspend fun getReviewReportList(
        boardRequest: BoardRequest
    ): Result<DataResponse<BoardThumbnailResponse, BoardThumbnailResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisThumbnail>>() {}

                val topList: List<BoardThumbnailResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsReviewReport: List<BoardThumbnailResponse> =
                    cmsReviewReportRepository.findCmsReviewReportSearchTextAndPaging(boardRequest.searchText, pageable).map {
                        it.toResponse()
                    }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsReviewReport,
                        pageable,
                        cmsReviewReportRepository.countCmsReviewReportSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()

                val topList: List<BoardThumbnailResponse> =
                    cmsReviewReportRepository.findByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc().map {
                        it.toResponse()
                    }.toList()

                val cmsReviewReport: List<BoardThumbnailResponse> =
                    cmsReviewReportRepository.findCmsReviewReportSearchTextAndPaging(boardRequest.searchText, pageable).map {
                        it.toResponse()
                    }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsReviewReport,
                        pageable,
                        cmsReviewReportRepository.countCmsReviewReportSearchTextAndPaging(boardRequest.searchText)
                    )
                )
            },
            afterJob = { dataResponse ->
                val redisReviewReport = dataResponse.fix.map {
                    it.toRedis()
                }

                redisOperator.setTopList(redisKey, redisReviewReport, RedisThumbnail::class.java)
            }
        )

    suspend fun getReviewReport(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsReviewReport = cmsReviewReportRepository.findByIdAndIsShowAndIsDraftAndIsDelete(id = id)

                val boardDetailResponse: BoardDetailResponse? = cmsReviewReport?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                redisOperator.publish(redisKey = REDIS_REVIEW_REPORT_READ_COUNT_KEY, id = id)
            }
        )
}
