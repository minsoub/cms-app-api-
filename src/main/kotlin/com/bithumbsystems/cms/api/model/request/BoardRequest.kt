package com.bithumbsystems.cms.api.model.request

import org.springframework.data.domain.PageRequest

data class BoardRequest(
    val categoryId: String?,
    val searchText: String?,
    val pageNo: Int,
    val pageSize: Int,
)

fun BoardRequest.toPageable() = PageRequest.of(pageNo - 1, pageSize)
