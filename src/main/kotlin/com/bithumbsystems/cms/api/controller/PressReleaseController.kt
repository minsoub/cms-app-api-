package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.service.PressReleaseService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "press_release", description = "보도자료 API")
@RestController
@RequestMapping("/press")
class PressReleaseController(
    private val pressReleaseService: PressReleaseService
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
    @Operation(method = "get", summary = "보도자료 리스트", description = "보도자료 고정 게시글 및 페이지에 해당하는 게시글 출력")
    @GetMapping("/list")
    suspend fun noticeList(
        @Parameter(name = "search_text", description = "검색어", `in` = ParameterIn.QUERY)
        @RequestParam(value = "search_text", required = false)
        searchText: String?,
        @Parameter(name = "page_no", description = "페이지 번호", `in` = ParameterIn.QUERY)
        @RequestParam(value = "page_no", required = false, defaultValue = "0")
        pageNo: Int,
        @Parameter(name = "page_size", description = "한페이지 당 갯수", `in` = ParameterIn.QUERY)
        @RequestParam(value = "page_size", required = false, defaultValue = "15")
        pageSize: Int
    ): ResponseEntity<Response<Any>> = execute {
        pressReleaseService.getPressReleaseList(searchText, pageNo, pageSize)
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
    @Operation(method = "get", summary = "보도자료 상세", description = "보도자료 상세 페이지")
    @GetMapping("/detail/{id}")
    suspend fun noticeDetail(
        @Parameter(name = "id", description = "게시글 id", `in` = ParameterIn.PATH)
        @PathVariable
        id: String
    ): ResponseEntity<Response<Any>> = execute {
        pressReleaseService.getPressRelease(id)
    }
}
