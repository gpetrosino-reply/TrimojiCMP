package it.reply.open.trimoji.data.remote.util

sealed interface ApiResult<out T: Any> {
    data class Success<T : Any>(val value: T) : ApiResult<T>

    sealed interface ApiFailure: ApiResult<Nothing>

    data class HttpError(
        val httpCode: Int,
        val errorBody: String,
    ): ApiFailure

    data class NetworkError(
        val cause: Throwable
    ): ApiFailure

    data class ContentError(
        val cause: Throwable
    ): ApiFailure

    data class OtherError(
        val cause: Throwable
    ): ApiFailure
}
