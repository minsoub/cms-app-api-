package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.config.operator.ServiceOperator.getCurrentRequestId
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.Logger
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisReadCountKey
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisNotice
import com.bithumbsystems.cms.persistence.redis.model.toRedis
import com.bithumbsystems.cms.persistence.redis.model.toRedisCategory
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsNoticeRepository: CmsNoticeRepository,
    private val cmsNoticeCategoryRepository: CmsNoticeCategoryRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {
    private val redisKey: String = RedisKey.REDIS_NOTICE_FIX_KEY
    private val logger by Logger()

    suspend fun getNoticeListInTitleContent(
        boardRequest: BoardRequest
    ): Result<DataResponse<NoticeFixResponse, NoticeResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisNotice>>() {}

                val topList: List<NoticeFixResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsNoticeList: List<NoticeResponse> =
                    cmsNoticeRepository.findCmsNoticeSearchTextAndPagingInTitleContent(boardRequest.categoryId, boardRequest.searchText, pageable)
                        .map {
                            it.toResponse()
                        }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPagingInTitleContent(boardRequest.categoryId, boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()

                val topList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc()

                val cmsNoticeList: List<NoticeResponse> = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(
                    categoryId = boardRequest.categoryId,
                    searchText = boardRequest.searchText,
                    pageable = pageable
                ).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    fix = getNoticeFixListWithCategory(topList),
                    list = PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText)
                    )
                )
            },
            afterJob = { dataResponses ->
                val redisNoticeFix = dataResponses.fix.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisNoticeFix, RedisNotice::class.java)
            }
        )

    suspend fun getNoticeList(
        boardRequest: BoardRequest
    ): Result<DataResponse<NoticeFixResponse, NoticeResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisNotice>>() {}

                val topList: List<NoticeFixResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsNoticeList: List<NoticeResponse> =
                    cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText, pageable)
                        .map {
                            it.toResponse()
                        }.toList()

                DataResponse(
                    fix = topList,
                    list = PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText)
                    )
                )
            },
            fallback = {
                val pageable = boardRequest.toPageable()

                val topList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowAndIsDraftAndIsDeleteOrderByScreenDateDesc()

                val cmsNoticeList: List<NoticeResponse> = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(
                    categoryId = boardRequest.categoryId,
                    searchText = boardRequest.searchText,
                    pageable = pageable
                ).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    fix = getNoticeFixListWithCategory(topList),
                    list = PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText)
                    )
                )
            },
            afterJob = { dataResponses ->
                val redisNoticeFix = dataResponses.fix.map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisNoticeFix, RedisNotice::class.java)
            }
        )

    suspend fun getNotice(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                logger.info("action")

                val cmsNotice = cmsNoticeRepository.findByIdAndIsShowAndIsDraftAndIsDelete(id = id)
                val category = cmsNotice?.categoryIds?.let { cmsNoticeCategoryRepository.findAllById(it) }

                val boardDetailResponse: BoardDetailResponse? = cmsNotice?.toDetailResponse()

                boardDetailResponse?.categoryNames = category?.map { it.name }?.toList()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }
                logger.info(getCurrentRequestId())
                boardDetailResponse
            },
            afterJob = {
                logger.info("afterJob")
                // 조회 수 카운트 작업
                logger.info(getCurrentRequestId())

                redisOperator.publish(redisKey = RedisReadCountKey.REDIS_NOTICE_READ_COUNT_KEY, id = id)
            }
        )

    suspend fun getNoticeFixListWithCategory(cmsNoticeFlow: Flow<CmsNotice>): List<NoticeFixResponse> {

        val category = cmsNoticeCategoryRepository.findAll().toList()

        return cmsNoticeFlow.map { cmsNotice ->
            NoticeFixResponse(
                id = cmsNotice.id,
                title = cmsNotice.title,
                createDate = cmsNotice.createDate,
                categoryNames = cmsNotice.categoryIds?.let {
                    category.filter { cmsNoticeCategory ->
                        it.contains(cmsNoticeCategory.id)
                    }.map {
                        it.name
                    }
                }
            )
        }.toList()
    }

    suspend fun getNoticeCategoryList(): Result<List<NoticeCategoryResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                redisOperator.getNoticeCategory()
            },
            fallback = {
                cmsNoticeCategoryRepository.findByIsUseTrueAndIsDeleteFalse().map {
                    it.toResponse()
                }.toList()
            },
            afterJob = { noticeCategoryResponses ->
                redisOperator.setNoticeCategory(noticeCategoryResponses.map { it.toRedisCategory() })
            }
        )
}
