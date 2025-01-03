package it.reply.open.trimoji.data.repository

import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.remote.QuestionsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TriviaRepository(
    private val dataSource: QuestionsDataSource,
    private val dispatcher: CoroutineDispatcher,
) {

    suspend fun retrieveQuestionSet(): List<Question> = withContext(dispatcher) {
        return@withContext dataSource.getTriviaQuestions(10)
    }
}