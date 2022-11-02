package com.bithumbsystems.cms.api.model.response

import com.bithumbsystems.cms.persistence.mongo.enums.ErrorCode
import com.bithumbsystems.cms.persistence.mongo.enums.ResponseCode

class Response<out T>(
    val result: ResponseCode = ResponseCode.SUCCESS,
    val data: T? = null
)

class ErrorData(
    val code: ErrorCode,
    val message: String?
)
