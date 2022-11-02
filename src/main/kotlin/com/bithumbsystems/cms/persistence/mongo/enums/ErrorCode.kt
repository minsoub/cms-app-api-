package com.bithumbsystems.cms.persistence.mongo.enums

enum class ErrorCode(val message: String) {
    UNKNOWN("Unknown Error"),
    ILLEGAL_ARGUMENT("ILLEGAL ARGUMENT"),
    ILLEGAL_STATE("ILLEGAL STATE")
}
