package com.bithumbsystems.cms.api.model.request

data class BoardRequest(
    val categoryId: String?,
    val searchText: String?,
    val pageNo: Int,
    val pageSize: Int,
)
