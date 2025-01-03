package it.reply.open.trimoji.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import it.reply.open.trimoji.data.model.ChatCompletionRequest
import it.reply.open.trimoji.data.model.ChatCompletionResponse

class OpenAIDataSource(
    val apiToken: String,
    val httpClient: HttpClient,
) {


    suspend fun postChatCompletion(body: ChatCompletionRequest): ChatCompletionResponse {
        return httpClient
            .post("""https://api.openai.com/v1/chat/completions""") {
                bearerAuth(apiToken)
                setBody(body)
            }
            .body()
    }
}