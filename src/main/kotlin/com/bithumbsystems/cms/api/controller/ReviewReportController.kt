package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.config.resolver.QueryParam
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.service.ReviewReportService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "review_report", description = "가상자산 게시판 API")
@RestController
@RequestMapping("/report")
class ReviewReportController(
    private val reviewReportService: ReviewReportService
) {
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(schema = Schema(implementation = BoardResponse::class))
                ]
            )
        ]
    )
    @Parameters(
        Parameter(
            description = "검색어",
            name = "search_text",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(implementation = String::class)
        ),
        Parameter(
            description = "페이지 번호",
            name = "page_no",
            `in` = ParameterIn.QUERY,
            schema = Schema(defaultValue = "0", implementation = Int::class),
            example = "0"
        ),
        Parameter(
            description = "페이지당 개시글 갯수",
            name = "page_size",
            `in` = ParameterIn.QUERY,
            schema = Schema(defaultValue = "12", implementation = Int::class),
            example = "12"
        ),
    )
    @Operation(method = "get", summary = "가상자산 리스트", description = "가상자산 고정 게시글 및 페이지에 해당하는 게시글 출력")
    @GetMapping("/list")
    suspend fun reviewReportList(
        @QueryParam
        @Parameter(hidden = true)
        boardRequest: BoardRequest
    ): ResponseEntity<Response<Any>> = execute {
        reviewReportService.getReviewReportList(boardRequest)
    }

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
    @Parameters(
        Parameter(description = "게시글 아이디", name = "id", `in` = ParameterIn.PATH, schema = Schema(implementation = String::class)),
    )
    @Operation(method = "get", summary = "가상자산 상세", description = "가상자산 상세 페이지")
    @GetMapping("/detail/{id}")
    suspend fun reviewReportDetail(
        @PathVariable
        id: String
    ): ResponseEntity<Response<Any>> = execute {
        reviewReportService.getReviewReport(id)
    }
}