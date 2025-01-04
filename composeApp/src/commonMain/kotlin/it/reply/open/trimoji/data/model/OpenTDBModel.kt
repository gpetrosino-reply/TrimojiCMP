package it.reply.open.trimoji.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
data class OpenTDBQuestionsResponse(
    @SerialName("response_code") val responseCode: Int,
    val results: List<Question>,
) {
    @Transient
    val responseCodeEnum: OpenTDBResponseCode = run {
        if (responseCode in 0..<OpenTDBResponseCode.entries.size) {
            OpenTDBResponseCode.entries[responseCode]
        } else {
            null
        }
    } ?: throw RuntimeException("Unexpected response code: $responseCode")


    enum class OpenTDBResponseCode {
        Success,
        NoResults,
        InvalidParameter,
        TokenNotFound,
        TokenEmpty,
        RateLimit,
    }
}

data class OpenTDBException(
    val responseCode: OpenTDBQuestionsResponse.OpenTDBResponseCode
): RuntimeException(responseCode.toString())


@Serializable
data class Question(
    val type: String,
    val difficulty: String,
    val category: String,
    @SerialName("question") val questionText: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("incorrect_answers") val incorrectAnswers: List<String>,
)



