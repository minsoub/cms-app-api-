package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.service.BoardService
import com.bithumbsystems.cms.api.config.operator.ServiceOperator.execute
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
    suspend fun getBoards(): Response<Any>? = execute {
        boardService.getList()
    }

    @GetMapping("/top")
    suspend fun getTopBoard(): Response<Any>? = execute {
        boardService.getTop()
    }

    @GetMapping("/{boardId}")
    suspend fun getBoard(@PathVariable boardId: String): Response<Any>? = execute {
        boardService.getOne(boardId)
    }
}
