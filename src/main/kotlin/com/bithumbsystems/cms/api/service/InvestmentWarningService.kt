package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.BoardDetailResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toDetailResponse
import com.bithumbsystems.cms.persistence.mongo.repository.CmsFileInfoRepository
import com.bithumbsystems.cms.persistence.mongo.repository.CmsInvestmentWarningRepository
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service

@Service
class InvestmentWarningService(
    private val cmsInvestmentWarningRepository: CmsInvestmentWarningRepository,
    private val cmsFileInfoRepository: CmsFileInfoRepository,
) {

    suspend fun getInvestmentWarning(): Result<Any?, ErrorData> =
        executeIn(
            action = {
                val cmsInvestmentWarning = cmsInvestmentWarningRepository
                    .findFirstByIsShowAndIsDeleteAndIsDraftOrderByScreenDateDesc()

                val boardDetailResponse: BoardDetailResponse? = cmsInvestmentWarning?.toDetailResponse()

                boardDetailResponse?.fileId?.let {
                    val fileInfo = cmsFileInfoRepository.findById(it)

                    boardDetailResponse.fileSize = fileInfo?.size
                    boardDetailResponse.fileName = "${fileInfo?.name}.${fileInfo?.extension}"
                }
                boardDetailResponse
            }
        )
}
