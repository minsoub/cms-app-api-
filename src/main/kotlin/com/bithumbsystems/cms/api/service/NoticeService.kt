package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
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
    suspend fun getNoticeList(
        categoryId: String?,
        searchText: String?,
        pageNo: Int,
        pageSize: Int
    ): Result<DataResponse?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val pageable = PageRequest.of(pageNo, pageSize)

                val topList = redisOperator.getTopNotice().map {
                    it.toResponse()
                }.toList()

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(categoryId, searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    topList,
                    PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(categoryId, searchText)
                    )
                )
            },
            fallback = {
                val pageable = PageRequest.of(pageNo, pageSize)

                val cmsNoticeTopList = cmsNoticeRepository.findCmsNoticeByIsFixTopAndIsShowOrderByScreenDateDesc().map {
                    it.toResponse()
                }.toList()

                val cmsNoticeList = cmsNoticeRepository.findCmsNoticeSearchTextAndPaging(categoryId, searchText, pageable).map {
                    it.toResponse()
                }.toList()

                DataResponse(
                    cmsNoticeTopList,
                    PageImpl(
                        cmsNoticeList,
                        pageable,
                        cmsNoticeRepository.countCmsNoticeSearchTextAndPaging(categoryId, searchText)
                    )
                )
            },
            afterJob = {
            }
        )

    suspend fun getNotice(id: String): Result<BoardDetailResponse?, ErrorData> =
        executeIn(
            action = {
                val cmsNotice = cmsNoticeRepository.findById(id)
                val category = cmsNotice?.categoryId?.let { cmsNoticeCategoryRepository.findAllById(it) }

                val boardDetailResponse: BoardDetailResponse? = cmsNotice?.toDetailResponse()

                boardDetailResponse?.categoryNames = category?.map { it.name }?.toList()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }

                boardDetailResponse
            }
        )

//    suspend fun getNoticeListWithCategory(boardResponseList: List<BoardResponse>): List<BoardResponse> {
//
//        val category = cmsNoticeCategoryRepository.findAll().toList()
//
//        boardResponseList.map {
//            it.categoryNames = category.filter { cmsNoticeCategory ->
//                it.categoryId!!.contains(cmsNoticeCategory.id)
//            }.map {
//                it.name
//            }
//        }
//
//        return boardResponseList
//    }

    suspend fun getNoticeCategoryList(): Result<List<NoticeCategoryResponse>?, ErrorData> =
        executeIn(
            action = {
                cmsNoticeCategoryRepository.findAll().map {
                    it.toResponse()
                }.toList().sortedBy {
                    it.id.toInt()
                }
            }
        )
}
