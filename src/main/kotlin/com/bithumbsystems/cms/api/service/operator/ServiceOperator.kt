package com.bithumbsystems.cms.api.service.operator

import com.bithumbsystems.cms.api.model.response.SingleResponse
import kotlinx.coroutines.*

object ServiceOperator {

    suspend fun <T : Any> execute(
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
        validator: (suspend () -> Boolean)?,
        job: suspend () -> T?,
        fallback: suspend () -> Unit,
        afterJob: suspend () -> Unit
    ): T? = runCatching {
        if (validator != null) {
            check(validator())
        }
        val result = job()
        supervisorScope {
            launch(dispatcher) {
                afterJob()
            }
        }
        result
    }.onSuccess {
        SingleResponse(data = it)
    }.onFailure {
        fallback()
    }.getOrThrow()
}
