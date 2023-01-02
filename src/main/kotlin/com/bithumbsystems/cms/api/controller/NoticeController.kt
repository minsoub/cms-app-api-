package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.config.resolver.QueryParam
import com.bithumbsystems.cms.api.model.request.BoardRequest
import com.bithumbsystems.cms.api.model.response.*
import com.bithumbsystems.cms.api.service.NoticeService
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

@Tag(name = "notice", description = "공지사항 게시판 API")
@RestController
@RequestMapping("/notice")
class NoticeController(
    private val noticeService: NoticeService
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
            description = "카테고리 아이디",
            name = "category_id",
            `in` = ParameterIn.QUERY,
            required = false,
            schema = Schema(implementation = String::class)
        ),
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
            schema = Schema(defaultValue = "15", implementation = Int::class),
            example = "15"
        ),
    )
    @Operation(method = "get", summary = "공지사항 리스트", description = "공지사항 고정 게시글 및 페이지에 해당하는 게시글 출력")
    @GetMapping("/list")
    suspend fun noticeList(
        @QueryParam
        @Parameter(hidden = true)
        boardRequest: BoardRequest
    ): ResponseEntity<Response<Any>> = execute {
        noticeService.getNoticeList(boardRequest)
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
    @Operation(method = "get", summary = "공지사항 상세", description = "공지사항 상세 페이지")
    @GetMapping("/detail/{id}")
    suspend fun noticeDetail(
        @PathVariable
        id: String
    ): ResponseEntity<Response<Any>> = execute {
        noticeService.getNotice(id)
    }

    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(schema = Schema(implementation = NoticeCategoryResponse::class))
                ]
            )
        ]
    )
    @Operation(method = "get", summary = "카테고리 리스트", description = "카테고리 리스트")
    @GetMapping("/category")
    suspend fun noticeCategory(): ResponseEntity<Response<Any>> = execute {
        noticeService.getNoticeCategoryList()
    }
}
