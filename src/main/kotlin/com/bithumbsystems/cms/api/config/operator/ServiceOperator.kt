package com.bithumbsystems.cms.api.config.operator

import com.bithumbsystems.cms.api.model.enums.ErrorCode
import com.bithumbsystems.cms.api.model.enums.ResponseCode
import com.bithumbsystems.cms.api.model.response.ErrorData
import com.bithumbsystems.cms.api.model.response.Response
import com.bithumbsystems.cms.api.util.Logger
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.recover
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.ReactorContext
import org.springframework.http.ResponseEntity
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object ServiceOperator {

    private val requestIdThreadLocal = ThreadLocal<String>()
    const val CONTEXT_NAME = "CMS_CONTEXT"

    private val logger by Logger()

    fun set(requestId: String) {
        requestIdThreadLocal.set(requestId)
    }

    fun getRequestId(): String {
        return requestIdThreadLocal.get()
    }

    suspend fun getCurrentRequestId(): String {
        val current = coroutineContext[ReactorContext]?.context?.get<String>(CONTEXT_NAME)!!

        return withContext(asContextElement(current)) {
            getRequestId()
        }
    }

    private fun clear() {
        requestIdThreadLocal.remove()
    }

    private fun asContextElement(requestId: String): CoroutineContext {
        return requestIdThreadLocal.asContextElement(requestId)
    }

    fun errorHandler(throwable: Throwable, errorCode: ErrorCode? = ErrorCode.ILLEGAL_STATE): ErrorData =
        errorCode?.let {
            ErrorData(
                code = errorCode,
                message = errorCode.message
            )
        } ?: when (throwable) {
            is IllegalArgumentException ->
                ErrorData(
                    code = ErrorCode.ILLEGAL_ARGUMENT,
                    message = ErrorCode.ILLEGAL_ARGUMENT.message
                )

            is IllegalStateException ->
                ErrorData(
                    code = ErrorCode.ILLEGAL_STATE,
                    message = ErrorCode.ILLEGAL_STATE.message
                )

            else -> {
                ErrorData(
                    code = ErrorCode.UNKNOWN,
                    message = ErrorCode.UNKNOWN.message
                )
            }
        }

    suspend fun <T> execute(
        block: suspend () -> Result<T?, ErrorData>
    ): ResponseEntity<Response<Any>> = withContext(
        asContextElement(coroutineContext[ReactorContext]?.context?.get<String>(CONTEXT_NAME)!!)
    ) {
        set(kotlin.coroutines.coroutineContext[ReactorContext]?.context?.get<String>(CONTEXT_NAME)!!)
        val result = block()
        clear()
        result.fold(
            success = { ResponseEntity.ok(Response(result = ResponseCode.SUCCESS, data = it)) },
            failure = { ResponseEntity.badRequest().body(Response(result = ResponseCode.ERROR, data = it)) }
        )
    }

    suspend fun <T> executeIn(
        validator: suspend () -> Boolean,
        action: suspend () -> T?
    ): Result<T?, ErrorData> = runSuspendCatching {
        require(validator())
        action()
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        action: suspend () -> T?
    ): Result<T?, ErrorData> = runSuspendCatching {
        action()
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        dispatcher: CoroutineDispatcher,
        action: suspend () -> T?
    ): Result<T?, ErrorData> = runSuspendCatching {
        withContext(dispatcher) {
            action()
        }
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        dispatcher: CoroutineDispatcher,
        action: suspend () -> T?,
        afterJob: suspend () -> Unit
    ): Result<T?, ErrorData> = runSuspendCatching {
        logger.debug("action start")
        val result = action()
        supervisorScope {
            launch(dispatcher) {
                result?.apply {
                    logger.debug("afterjob start")
                    afterJob()
                }
            }
        }
        result
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        dispatcher: CoroutineDispatcher,
        validator: suspend () -> Boolean,
        action: suspend () -> T?,
        fallback: suspend () -> T?,
        afterJob: suspend (T) -> Unit
    ): Result<T?, ErrorData> = runSuspendCatching {
        require(validator())
        logger.debug("action start")
        action()
    }.recover {
        logger.debug("fallback start")
        val result = fallback()
        supervisorScope {
            launch(dispatcher) {
                result?.apply {
                    logger.debug("afterjob start")
                    afterJob(result)
                }
            }
        }
        result
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        dispatcher: CoroutineDispatcher,
        action: suspend () -> T?,
        fallback: suspend () -> T?,
        afterJob: suspend (T) -> Unit
    ): Result<T?, ErrorData> = runSuspendCatching {
        logger.debug("action start")
        action()
    }.recover {
        logger.debug("fallback start")
        val result = fallback()
        supervisorScope {
            launch(dispatcher) {
                result?.apply {
                    logger.debug("afterjob start")
                    afterJob(result)
                }
            }
        }
        result
    }.mapError {
        errorHandler(it)
    }

    suspend fun <T> executeIn(
        dispatcher: CoroutineDispatcher,
        action: suspend () -> T?,
        fallback: suspend () -> T?,
        afterJob: suspend (T) -> Unit,
        finally: suspend (T) -> Unit
    ): Result<T?, ErrorData> = runSuspendCatching {
        logger.debug("action start")
        val result = action()
        supervisorScope {
            launch(dispatcher) {
                result?.apply {
                    logger.debug("afterjob start")
                    finally(result)
                }
            }
        }
        result
    }.recover {
        logger.debug("fallback start")
        val result = fallback()
        supervisorScope {
            launch(dispatcher) {
                result?.apply {
                    logger.debug("afterjob start")
                    afterJob(result)
                    finally(result)
                }
            }
        }
        result
    }.mapError {
        errorHandler(it)
    }
}
