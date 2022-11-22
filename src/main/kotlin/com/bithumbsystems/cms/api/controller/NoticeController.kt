package com.bithumbsystems.cms.api.controller

import com.bithumbsystems.cms.api.config.operator.ServiceOperator
import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.service.NoticeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notice")
class NoticeController(
    private val noticeService: NoticeService
) {

    @GetMapping()
    suspend fun getAll(): ResponseEntity<Response<Any>> = ServiceOperator.execute {
        noticeService.getAll()
    }

    @PostMapping()
    suspend fun insertOne(): ResponseEntity<Response<Any>> = ServiceOperator.execute {
        noticeService.insertOne()
    }
}
