package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.request.BannerRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.util.RedisKey
import com.bithumbsystems.cms.api.util.RedisRecentKey
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.bithumbsystems.cms.persistence.redis.model.RedisBanner
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class MainService(
    private val ioDispatcher: CoroutineDispatcher,
    private val redisOperator: RedisOperator,
    private val noticeRepository: CmsNoticeRepository
) {
    private val redisBannerKey: String = RedisKey.REDIS_NOTICE_BANNER_KEY

    suspend fun getMobileBanner(): Result<List<BannerResponse>?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val typeReference = object : TypeReference<List<RedisBanner>>() {}

                val banner: List<BannerResponse> = redisOperator.getTopList(redisBannerKey, typeReference).map { it.toResponse() }

                banner
            },
            fallback = {
                val banner = noticeRepository.findCmsNoticeByIsBannerAndIsShow().map { it.toBannerResponse() }.toList()

                banner
            },
            afterJob = {
                val banner = noticeRepository.findCmsNoticeByIsBannerAndIsShow().map { it.toBannerResponse() }.toList()

                redisOperator.setTopList(redisBannerKey, banner, BannerResponse::class.java)
            }
        )

    suspend fun getMainRecentBoard(
        bannerRequest: BannerRequest
    ): Result<Any?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val redisKey = when (bannerRequest.boardType) {
                    RedisRecentKey.CMS_NOTICE_RECENT -> "CMS_NOTICE_RECENT"
                    RedisRecentKey.CMS_PRESS_RELEASE_RECENT -> "CMS_PRESS_RELEASE_RECENT"
                }
            },
            fallback = {
            },
            afterJob = {
            }
        )
}
