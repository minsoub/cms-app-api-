package com.bithumbsystems.cms.api.model.mongo.repository

import com.bithumbsystems.cms.api.model.mongo.entity.CmsNotice
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CmsNoticeRepository : ReactiveMongoRepository<CmsNotice, String>

