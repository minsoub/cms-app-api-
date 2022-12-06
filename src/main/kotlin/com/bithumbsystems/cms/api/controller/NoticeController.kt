package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
import com.bithumbsystems.cms.api.model.response.BoardDetailResponse
import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.service.NoticeService
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

@Tag(name = "notice", description = "공지사항 게시판 API")
@RestController
@RequestMapping("/notice")
class NoticeController(
    private val noticeService: NoticeService
) {

//    @GetMapping("/add")
//    suspend fun noticeAdd(): ResponseEntity<Response<Any>> = execute {
//        noticeService.insertOne()
//    }

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
    @Operation(method = "get", summary = "공지사항 리스트", description = "공지사항 고정 게시글 및 페이지에 해당하는 게시글 출력")
    @GetMapping("/list")
    suspend fun noticeList(
        @Parameter(name = "category_id", description = "공지사항 카테고리 id", `in` = ParameterIn.QUERY)
        @RequestParam(value = "category_id", required = false)
        categoryId: String?,
        @Parameter(name = "search_text", description = "공지사항 검색어", `in` = ParameterIn.QUERY)
        @RequestParam(value = "search_text", required = false)
        searchText: String?,
        @Parameter(name = "page_no", description = "페이지 번호", `in` = ParameterIn.QUERY)
        @RequestParam(value = "page_no", required = false, defaultValue = "0")
        pageNo: Int,
        @Parameter(name = "page_size", description = "한페이지 당 갯수", `in` = ParameterIn.QUERY)
        @RequestParam(value = "page_size", required = false, defaultValue = "15")
        pageSize: Int
    ): ResponseEntity<Response<Any>> = execute {
        noticeService.getNoticeList(categoryId, searchText, pageNo, pageSize)
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
    @Operation(method = "get", summary = "공지사항 상세", description = "공지사항 상세 페이지")
    @GetMapping("/detail/{id}")
    suspend fun noticeDetail(
        @Parameter(name = "id", description = "공지사항 게시글 id", `in` = ParameterIn.PATH)
        @PathVariable
        id: String
    ): ResponseEntity<Response<Any>> = execute {
        noticeService.getNotice(id)
    }
}
