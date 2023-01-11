package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.BoardDetailResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toDetailResponse
import com.bithumbsystems.cms.api.util.RedisKey.REDIS_INVESTMENT_WARNING_FIX_KEY
import com.bithumbsystems.cms.api.util.RedisReadCountKey.REDIS_INVESTMENT_WARNING_READ_COUNT_KEY
import com.bithumbsystems.cms.persistence.mongo.entity.CmsInvestmentWarning
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsInvestmentWarningRepository
import com.bithumbsystems.cms.persistence.redis.RedisOperator
import com.fasterxml.jackson.core.type.TypeReference
import com.github.michaelbull.result.Result
import kotlinx.coroutines.CoroutineDispatcher
import org.springframework.stereotype.Service

@Service
class InvestmentWarningService(
    private val ioDispatcher: CoroutineDispatcher,
    private val cmsInvestmentWarningRepository: CmsInvestmentWarningRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
    private val redisOperator: RedisOperator
) {

    suspend fun getInvestmentWarning(): Result<Any?, ErrorData> =
        executeIn(
            dispatcher = ioDispatcher,
            action = {
                val typeReference = object : TypeReference<String>() {}
                val id: String = redisOperator.getOne(REDIS_INVESTMENT_WARNING_FIX_KEY, typeReference)
                val cmsInvestmentWarning = cmsInvestmentWarningRepository
                    .findById(id)

                toBoardDetailResponse(cmsInvestmentWarning)
            },
            fallback = {
                val cmsInvestmentWarning = cmsInvestmentWarningRepository
                    .findFirstByIsShowAndIsDeleteAndIsDraftOrderByScreenDateDesc()

                toBoardDetailResponse(cmsInvestmentWarning)
            },
            afterJob = {
                redisOperator.setOne(redisKey = REDIS_INVESTMENT_WARNING_FIX_KEY, value = it.id)
            },
            finally = {
                redisOperator.publish(redisKey = REDIS_INVESTMENT_WARNING_READ_COUNT_KEY, id = it.id)
            }
        )

    private suspend fun toBoardDetailResponse(cmsInvestmentWarning: CmsInvestmentWarning?): BoardDetailResponse? {
        val boardDetailResponse: BoardDetailResponse? = cmsInvestmentWarning?.toDetailResponse()

        boardDetailResponse?.fileId?.let {
            val fileInfo = cmsFileInfoRepository.findById(it)

            boardDetailResponse.fileSize = fileInfo?.size
            boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
        }
        return boardDetailResponse
    }
}
