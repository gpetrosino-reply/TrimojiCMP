package it.reply.open.trimoji.data.cache

import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.QuestionsDataSource
import it.reply.open.trimoji.data.remote.RemoteQuestionsDataSource
import it.reply.open.trimoji.data.remote.util.ApiResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MemCachedQuestionsDataSource(
    private val remoteSource: RemoteQuestionsDataSource,
    private val blacklistWords: List<String>,
) : QuestionsDataSource {

    companion object {
        const val MAX_ATTEMPTS = 5
    }

    private val queueMutex = Mutex()
    private var queue: List<Question> = listOf()

    override suspend fun getTriviaQuestions(amount: Int): ApiResult<List<Question>> = queueMutex.withLock {
        var attempts = 0
        var lastAttemptFailure: ApiResult.ApiFailure? = null
        val questions = buildList {
            while (size < amount) {
                val elem = queue.firstOrNull()

                if (elem == null && attempts >= MAX_ATTEMPTS) {
                    lastAttemptFailure?.let { return@withLock it }
                    throw IllegalStateException("Something went wrong while trying to build a queue of questions")
                }

                if (elem == null) {
                    attempts++
                    lastAttemptFailure = pullFromRemote()
                    continue
                }

                queue = queue.drop(1)

                if (blacklistWords.any { badWord -> elem.questionText.lowercase().contains(badWord) }) {
                    continue //skip this question
                }

                attempts = 0
                add(elem)
            }

        }
        ApiResult.Success(questions)
    }

    /**
     *
     *
     * @return an [ApiResult.ApiFailure] if something wrong happens, null otherwise
     */
    private suspend fun pullFromRemote(): ApiResult.ApiFailure? {
        val triviaQuestionsResult = remoteSource.getTriviaQuestions(40)
        when (triviaQuestionsResult) {
            is ApiResult.ApiFailure -> return triviaQuestionsResult
            is ApiResult.Success -> {
                // Continue
            }
        }
        queue += triviaQuestionsResult.value
        return null
    }


}