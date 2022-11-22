package com.bithumbsystems.cms.api.service

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.executeIn
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.toResponse
import com.bithumbsystems.cms.persistence.mongo.entity.CmsNotice
import com.bithumbsystems.cms.persistence.mongo.repository.CmsNoticeRepository
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val cmsNoticeRepository: CmsNoticeRepository
) {

    suspend fun getAll(): Result<List<BoardResponse>?, ErrorData> =
        executeIn(
            action = {
                cmsNoticeRepository.findAll().map {
                    it.toResponse()
                }.toList()
            }
        )
    suspend fun insertOne(): Result<BoardResponse?, ErrorData> =
        executeIn(
            action = {
                cmsNoticeRepository.save(
                    CmsNotice(title = "test", categoryId = listOf("test"), createAccountId = "testAccount")
                ).toResponse()
            }
        )
}
