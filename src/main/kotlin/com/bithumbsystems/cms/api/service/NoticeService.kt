package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.toNoticeFix
import com.bithumbsystems.cms.persistence.redis.model.toRedisCategory
import com.github.michaelbull.result.Result
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsNoticeRepository: CmsNoticeRepository,
    private val cmsNoticeCategoryRepository: CmsNoticeCategoryRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator,
) {
    private val redisKey: String = RedisKey.REDIS_NOTICE_FIX_KEY

    suspend fun getNoticeList(
        boardRequest: BoardRequest
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                val topList: List<BoardResponse> = redisOperator.getTopList(redisKey).map { it.toResponse() }

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText, pageable)
                    .map {
                        it.toResponse()
                    }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText).awaitSingle()
                    )
                )
            },
            fallback = {
                val pageable = PageRequest.of(boardRequest.pageNo, boardRequest.pageSize)

                var topList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                topList = getNoticeFixListWithCategory(topList)

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText, pageable)
                    .map {
                        it.toResponse()
                    }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(boardRequest.categoryId, boardRequest.searchText).awaitSingle()
                    )
                )
            },
            afterJob = {
                var fixList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                fixList = getNoticeFixListWithCategory(fixList)

                val redisNoticeFix = fixList.map {
                    it.toNoticeFix()
                }
                redisOperator.setTopList(redisKey, redisNoticeFix)
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
                val cmsNotice = cmsNoticeRepository.findById(id)

                cmsNotice?.let {
                    // 조회 수 카운트 작업
                }
            }
        )

    suspend fun getNoticeFixListWithCategory(boardResponseList: List<BoardResponse>): List<BoardResponse> {

        val category = cmsNoticeCategoryRepository.findAll().toList()

        boardResponseList.map {
            it.categoryNames = category.filter { cmsNoticeCategory ->
                it.categoryIds!!.contains(cmsNoticeCategory.id)
            }.map {
                it.name
            }
        }
        return boardResponseList
    }

    suspend fun getNoticeCategoryList(): Result<List<NoticeCategoryResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                redisOperator.getNoticeCategory()
            },
            fallback = {
                cmsNoticeCategoryRepository.findAll().map {
                    it.toResponse()
                }.toList()
            },
            afterJob = {
                val categoryList = cmsNoticeCategoryRepository.findAll().map {
                    it.toRedisCategory()
                }.toList()

                redisOperator.setNoticeCategory(categoryList)
            }
        )
}
