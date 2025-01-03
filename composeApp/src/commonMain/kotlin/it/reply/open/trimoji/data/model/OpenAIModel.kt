package it.reply.open.trimoji.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ChatCompletionRole {
    system,
    user,
    developer,
    assistant,
}

@Serializable
data class ChatCompletionMessage(
    val role: ChatCompletionRole,
    val content: String,
)

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatCompletionMessage>,
    val n: Int = 1,
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val choices: List<ChatCompletionChoice>,
    val created: Long,
    val model: String,
)

@Serializable
data class ChatCompletionChoice(
    @SerialName("finish_reason") val finishReason: String,
    val index: Int,
    val message: ChatCompletionMessage,
)


