package it.reply.open.trimoji.data

import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.remote.util.ApiResult

interface QuestionsDataSource {

    suspend fun getTriviaQuestions(amount: Int): ApiResult<List<Question>>
}