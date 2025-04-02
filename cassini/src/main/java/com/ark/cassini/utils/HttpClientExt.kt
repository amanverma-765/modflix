package com.ark.cassini.utils

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import java.net.UnknownHostException
import kotlin.coroutines.coroutineContext


internal suspend inline fun <reified T> safeRequest(
    execute: () -> HttpResponse
): T? {
    val response = try {
        execute()
    } catch (e: UnknownHostException) {
        return null
    } catch (ex: SocketTimeoutException) {
        return null
    } catch (ex: UnresolvedAddressException) {
        return null
    } catch (ex: Exception) {
        coroutineContext.ensureActive()
        return null
    }

    return handleResponse(response)
}


internal suspend inline fun <reified T> safeRequestWithCallback(
    execute: () -> HttpResponse,
    crossinline onError: (ErrorType) -> Unit
): T? {
    val response = try {
        execute()
    } catch (ex: SocketTimeoutException) {
        onError(ErrorType.TIMEOUT)
        return null
    } catch (ex: UnresolvedAddressException) {
        onError(ErrorType.NO_INTERNET)
        return null
    } catch (ex: Exception) {
        coroutineContext.ensureActive()
        onError(ErrorType.UNKNOWN)
        return null
    }

    return when (response.status.value) {
        in 200..299 -> {
            try {
                response.body<T>()
            } catch (e: NoTransformationFoundException) {
                onError(ErrorType.SERIALIZATION_ERROR)
                null
            }
        }

        408 -> {
            onError(ErrorType.TIMEOUT)
            null
        }

        429 -> {
            onError(ErrorType.TOO_MANY_REQUESTS)
            null
        }

        404 -> {
            onError(ErrorType.NOT_FOUND)
            null
        }

        in 500..599 -> {
            onError(ErrorType.SERVER_ERROR)
            null
        }

        else -> {
            onError(ErrorType.UNKNOWN)
            null
        }
    }
}


private suspend inline fun <reified T> handleResponse(response: HttpResponse): T? {
    return when (response.status.value) {
        in 200..299 -> {
            try {
                response.body<T>()
            } catch (e: NoTransformationFoundException) {
                null
            }
        }

        else -> null
    }
}


enum class ErrorType {
    TIMEOUT,
    NO_INTERNET,
    SERIALIZATION_ERROR,
    TOO_MANY_REQUESTS,
    NOT_FOUND,
    SERVER_ERROR,
    UNKNOWN
}