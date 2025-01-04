package it.reply.open.trimoji.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.setBody
import it.reply.open.trimoji.data.model.ChatCompletionRequest
import it.reply.open.trimoji.data.model.ChatCompletionResponse
import it.reply.open.trimoji.data.remote.util.ApiResult
import it.reply.open.trimoji.data.remote.util.safePost

class OpenAIDataSource(
    val apiToken: String,
    val httpClient: HttpClient,
) {
    suspend fun postChatCompletion(body: ChatCompletionRequest): ApiResult<ChatCompletionResponse> {
        return httpClient.safePost("""https://api.openai.com/v1/chat/completions""") {
            bearerAuth(apiToken)
            setBody(body)
        }
    }
}