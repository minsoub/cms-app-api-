package com.bithumbsystems.cms.api.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonUtil {
    val gson: Gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
}
