package com.bithumbsystems.cms.persistence.mongo.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document("cpc_boards")
class Test(title: String?) {
    @Id
    var id: String = UUID.randomUUID().toString()
    var title: String? = title
}
