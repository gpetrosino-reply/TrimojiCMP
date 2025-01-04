package it.reply.open.trimoji.data.remote.util

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun <T : Any, R : Any> ApiResult<T>.map(
    transform: (T) -> R,
): ApiResult<R> {
    contract {
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is ApiResult.Success -> {
            ApiResult.Success(this.value.let(transform))
        }

        is ApiResult.ApiFailure -> {
            this
        }
    }
}