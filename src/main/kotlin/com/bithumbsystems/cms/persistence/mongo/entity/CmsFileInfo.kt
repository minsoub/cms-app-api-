package com.bithumbsystems.cms.persistence.mongo.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime

@Document("cms_file_info")
class CmsFileInfo(
    @MongoId
    val id: String,
    val name: String,
    val size: Int,
    val extension: String,
    val createDate: LocalDateTime,
    val createAccountId: String,
    val createAccountEmail: String
)
