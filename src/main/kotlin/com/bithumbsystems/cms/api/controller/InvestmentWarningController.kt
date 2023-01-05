package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.service.InvestmentWarningService
import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.model.response.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "investment", description = "투자유의 게시판 API")
@RestController
@RequestMapping("/investment")
class InvestmentWarningController(
    private val investmentWarningService: InvestmentWarningService

) {
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(schema = Schema(implementation = BoardDetailResponse::class))
                ]
            )
        ]
    )
    @Operation(method = "get", summary = "투자유의 상세", description = "투자유의 상세 페이지")
    @GetMapping("")
    suspend fun investmentWarningDetail(): ResponseEntity<Response<Any>> = execute {
        investmentWarningService.getInvestmentWarning()
    }
}
