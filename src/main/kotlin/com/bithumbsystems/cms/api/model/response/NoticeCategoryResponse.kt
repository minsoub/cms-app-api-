package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.CmsNoticeCategory

data class NoticeCategoryResponse(
    val id: String,
    val name: String
) {
    constructor() : this("", "")
}

fun CmsNoticeCategory.toResponse() = NoticeCategoryResponse(
    id = id,
    name = name
)
