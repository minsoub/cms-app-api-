package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.entity.Test

data class TestResponse(
    val title: String?
) {
    fun toEntity(): Test = Test(
        title
    )
}

fun Test.toResponse() = TestResponse(
    title
)
