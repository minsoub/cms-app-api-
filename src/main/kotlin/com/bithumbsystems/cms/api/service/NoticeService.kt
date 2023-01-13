package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.request.toPageable
import com.bithumbsystems.cms.api.model.response.*
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

    suspend fun getNoticeList(
        boardRequest: BoardRequest
    ): Result<DataResponse<BoardResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = boardRequest.toPageable()

                val typeReference = object : TypeReference<List<RedisNotice>>() {}

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey, typeReference).map { it.toResponse() }

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText, pageable)
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

                val topList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc()

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText, pageable)
                    .map {
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
            afterJob = {
                val fixList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc()

                val redisNoticeFix = getNoticeFixListWithCategory(cmsNoticeFlow = fixList).map {
                    it.toRedis()
                }
                redisOperator.setTopList(redisKey, redisNoticeFix, RedisNotice::class.java)
            }
        )

    suspend fun getNotice(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val cmsNotice = cmsNoticeRepository.findById(id)
                val category = cmsNotice?.categoryIds?.let { cmsNoticeCategoryRepository.findAllById(it) }

                val boardDetailResponse: BoardDetailResponse? = cmsNotice?.toDetailResponse()

                boardDetailResponse?.categoryNames = category?.map { it.name }?.toList()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            },
            afterJob = {
                // 조회 수 카운트 작업
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
