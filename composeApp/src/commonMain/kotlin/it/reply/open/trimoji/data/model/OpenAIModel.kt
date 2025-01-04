package it.reply.open.trimoji.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
enum class ChatCompletionRole {
    system,
    user,
    developer,
    assistant,
}

@Serializable(
    with = OpenAIModel.Serializer::class
)
enum class OpenAIModel(val code: String) {
    GPT35Turbo("gpt-3.5-turbo"),
    GPT4o("gpt-4o-2024-08-06")
    ;

    companion object {
        val default = GPT35Turbo

    }

    object Serializer : KSerializer<OpenAIModel> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
            serialName = "it.reply.open.trimoji.data.model.OpenAIModel",
            kind = PrimitiveKind.STRING,
        )

        override fun serialize(encoder: Encoder, value: OpenAIModel) {
            encoder.encodeString(value.code)
        }

        override fun deserialize(decoder: Decoder): OpenAIModel {
            val string = decoder.decodeString()
            return OpenAIModel.entries.find { it.code == string } ?: OpenAIModel.default
        }
    }
}

@Serializable
data class ChatCompletionMessage(
    val role: ChatCompletionRole,
    val content: String,
)

@Serializable
data class ChatCompletionRequest(
    val model: OpenAIModel,
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




