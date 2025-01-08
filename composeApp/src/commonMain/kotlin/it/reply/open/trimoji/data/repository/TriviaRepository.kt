package it.reply.open.trimoji.data.repository

import it.reply.open.trimoji.data.QuestionsDataSource
import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.remote.util.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

//TODO rename
class TriviaRepository(
    private val dataSource: QuestionsDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend fun retrieveQuestionSet(amount: Int): ApiResult<List<Question>> = withContext(ioDispatcher) {
        return@withContext dataSource.getTriviaQuestions(amount)
    }
}