package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.config.resolver.QueryParam
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.BoardDetailResponse
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.service.EconomicResearchService
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "lab", description = "경제연구소 게시판 API")
@RestController
@RequestMapping("/lab")
class EconomicResearchController(
    private val economicResearchService: EconomicResearchService
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
            name = "searchText",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(implementation = String::class)
        ),
        Parameter(
            description = "페이지 번호",
            name = "pageNo",
            `in` = ParameterIn.QUERY,
            schema = Schema(defaultValue = "0", implementation = Int::class),
            example = "0"
        ),
        Parameter(
            description = "페이지당 개시글 갯수",
            name = "pageSize",
            `in` = ParameterIn.QUERY,
            schema = Schema(defaultValue = "15", implementation = Int::class),
            example = "15"
        ),
    )
    @Operation(method = "get", summary = "경제연구소 리스트", description = "경제연구소 고정 게시글 및 페이지에 해당하는 게시글 출력")
    @GetMapping("/list")
    suspend fun economicResearchList(
        @QueryParam
        @Parameter(hidden = true)
        boardRequest: BoardRequest
    ): ResponseEntity<Response<Any>> = execute {
        economicResearchService.getEconomicResearchList(boardRequest)
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
    @Operation(method = "get", summary = "경제연구소 상세", description = "경제연구소 상세 페이지")
    @GetMapping("/detail/{id}")
    suspend fun economicResearchDetail(
        @PathVariable
        id: String
    ): ResponseEntity<Response<Any>> = execute {
        economicResearchService.getEconomicResearch(id)
    }
}
