package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.model.response.BoardResponse
import com.bithumbsystems.cms.api.model.response.MultiResponse
import com.bithumbsystems.cms.api.model.response.SingleResponse
import com.bithumbsystems.cms.api.service.BoardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/boards")
class BoardController(
    private val boardService: BoardService
) {

    @GetMapping
    suspend fun getBoards(): MultiResponse<BoardResponse> =
        MultiResponse(data = boardService.getList())

    @GetMapping("/{boardId}")
    suspend fun getBoard(@PathVariable boardId: String): SingleResponse<BoardResponse?> =
        SingleResponse(data = boardService.getOne(boardId))
}
