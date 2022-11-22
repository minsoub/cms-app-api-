package com.bithumbsystems.cms.persistence.mongo.entity

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Document(collection = "cms_notice")
class CmsNotice(
    val categoryId: List<String>,
    val title: String,
    val isFixTop: Boolean = false,
    val isShow: Boolean = true,
    val isDelete: Boolean = false,
    val isBanner: Boolean = false,
    val content: String? = null,
    val fileId: String? = null,
    val shareTitle: String? = null,
    val shareDescription: String? = null,
    val shareFileId: String? = null,
    val shareButtonName: String? = null,
    val isSchedule: Boolean = false,
    val scheduleDate: Long? = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
    val isDraft: Boolean = false,
    val createAccountId: String,
    val createDate: Long? = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
) {
    @MongoId(targetType = FieldType.STRING)
    var id: String? = UUID.randomUUID().toString().replace("-", "")
    var readCount: Long = 0
    var updateAccountId: String? = null
    var updateDate: Long? = null
    var useUpdateDate: Boolean = false
    var isAlignTop: Boolean = false
    var screenDate: Long? = null
}
