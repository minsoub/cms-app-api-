package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.config.resolver.QueryParam
import com.bithumbsystems.cms.api.model.request.BannerRequest
import com.bithumbsystems.cms.api.model.response.BannerResponse
import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.service.MainService
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "main", description = "메인 배너, 메인 페이지 게시글 API")
@RestController
@RequestMapping("/main")
class MainController(
    private val mainService: MainService
) {

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(schema = Schema(implementation = BannerResponse::class))
                ]
            )
        ]
    )
    @GetMapping("/banner")
    suspend fun mobileBanner(): ResponseEntity<Response<Any>> = execute {
        mainService.getMobileBanner()
    }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(schema = Schema(implementation = BannerResponse::class))
                ]
            )
        ]
    )
    @Parameters(
        Parameter(
            description = "게시판 종류",
            name = "boardType",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(implementation = String::class)
        ),
        Parameter(
            description = "게시글 갯수",
            name = "pageSize",
            `in` = ParameterIn.QUERY,
            schema = Schema(defaultValue = "2", implementation = Int::class),
            example = "2"
        ),
    )
    @GetMapping("/recent")
    suspend fun mainRecentBoard(
        @QueryParam
        @Parameter(hidden = true)
        bannerRequest: BannerRequest
    ): ResponseEntity<Response<Any>> = execute {
        mainService.getMainRecentBoard(bannerRequest)
    }
}
