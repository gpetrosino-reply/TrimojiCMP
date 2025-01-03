package it.reply.open.trimoji.data.remote

import it.reply.open.trimoji.data.model.ApiException
import it.reply.open.trimoji.data.model.Question
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

    override suspend fun getTriviaQuestions(amount: Int): List<Question> = queueMutex.withLock {
        var attempts = 0
        return@withLock buildList {
            while (size < amount) {
                val elem = queue.firstOrNull()

                if (elem == null && attempts >= MAX_ATTEMPTS) {
                    throw ApiException("Unable to build a question set of $amount questions after $attempts attempts.")
                }

                if (elem == null) {
                    attempts++
                    pullFromRemote()
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

    }

    private suspend fun pullFromRemote() {
        queue += remoteSource.getTriviaQuestions(40)
    }


}