package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BannerRequest
import com.bithumbsystems.cms.api.model.response.BannerResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toBannerResponse
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.api.util.RedisKey.REDIS_NOTICE_BANNER_KEY
import com.bithumbsystems.cms.api.util.RedisRecentKey
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeCategoryRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsPressReleaseRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisBanner
import com.bithumbsystems.cms.persistence.redis.model.toRedis
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MainService(
    private val ioDispatcher: CoroutineDispatcher,
    private val redisOperator: RedisOperator,
    private val noticeRepository: CmsNoticeRepository,
    private val cmsPressReleaseRepository: CmsPressReleaseRepository,
    private val cmsNoticeCategoryRepository: CmsNoticeCategoryRepository
) {

    companion object {
        const val MAX_SIZE = 5
    }

    suspend fun getMobileBanner(): Result<List<BannerResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val typeReference = object : TypeReference<List<RedisBanner>>() {}
                val banner: List<BannerResponse> = redisOperator.getTopList(REDIS_NOTICE_BANNER_KEY, typeReference).map { it.toResponse() }
                banner
            },
            fallback = {
                val categoryMap = cmsNoticeCategoryRepository.findAll().map {
                    it.id to it.name
                }.toList().toMap()
                val banner = noticeRepository.findCmsNoticeByIsBannerAndIsShowAndIsDraftAndIsDelete().map {
                    makeToBannerResponse(it, categoryMap)
                }.toList()
                banner
            },
            afterJob = { bannerResponses ->
                redisOperator.setTopList(REDIS_NOTICE_BANNER_KEY, bannerResponses.map { it.toRedis() }, RedisBanner::class.java)
            }
        )

    suspend fun getMainRecentBoard(
        bannerRequest: BannerRequest
    ): Result<List<BannerResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            validator = { bannerRequest.pageSize > MAX_SIZE },
            action = {
                val key = bannerRequest.boardType.key

                val typeReference = object : TypeReference<List<RedisBanner>>() {}

                val recentList: List<BannerResponse> = redisOperator.getTopList(key, typeReference).map { it.toResponse() }

                recentList.subList(0, bannerRequest.pageSize)
            },
            fallback = {
                val pageable = PageRequest.of(0, MAX_SIZE)
                if (bannerRequest.boardType.key == RedisRecentKey.PRESS_RELEASE.name) {
                    cmsPressReleaseRepository.findCmsPressReleasePaging(pageable).map { it.toBannerResponse() }.toList()
                } else {
                    val categoryMap = cmsNoticeCategoryRepository.findAll().map {
                        it.id to it.name
                    }.toList().toMap()
                    noticeRepository.findCmsNoticePaging(pageable).map {
                        makeToBannerResponse(it, categoryMap)
                    }.toList()
                }
            },
            afterJob = { recentList ->
                redisOperator.setTopList(bannerRequest.boardType.key, recentList.map { it.toRedis() }, RedisBanner::class.java)
            }
        )

    private fun makeToBannerResponse(
        it: CmsNotice,
        categoryMap: Map<String, String>
    ): BannerResponse {
        val categoryTitle = it.categoryIds?.map { id ->
            categoryMap[id]
        }?.joinToString("/", "[", "]")
        return it.toBannerResponse(title = (categoryTitle + it.title).trim())
    }
}
