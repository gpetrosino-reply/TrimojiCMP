package it.reply.open.trimoji.data.remote.util

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <reified T : Any> HttpClient.safePost(
    urlString: String? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): ApiResult<T> = safeRequest(
    urlString = urlString,
) {
    method = HttpMethod.Post
    block()
}

suspend inline fun <reified T : Any> HttpClient.safeGet(
    urlString: String? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): ApiResult<T> = safeRequest(
    urlString = urlString,
) {
    method = HttpMethod.Get
    block()
}


suspend inline fun <reified T : Any> HttpClient.safeRequest(
    urlString: String? = null,
    block: HttpRequestBuilder.() -> Unit = {},
): ApiResult<T> {
    return try {
        val response = request {
            if (urlString != null) {
                url(urlString)
            }
            block()
        }
        ApiResult.Success(value = response.body())
    } catch (e: ClientRequestException) {
        ApiResult.HttpError(
            httpCode = e.response.status.value,
            errorBody = e.response.bodyAsText()
        )
    } catch (e: ServerResponseException) {
        ApiResult.HttpError(
            httpCode = e.response.status.value,
            errorBody = e.response.bodyAsText()
        )
    } catch (e: IOException) {
        ApiResult.NetworkError(cause = e)
    } catch (e: SerializationException) {
        ApiResult.ContentError(cause = e)
    }
}