package it.reply.open.trimoji.data.remote

import it.reply.open.trimoji.data.model.Question

interface QuestionsDataSource {

    suspend fun getTriviaQuestions(amount: Int): List<Question>
}