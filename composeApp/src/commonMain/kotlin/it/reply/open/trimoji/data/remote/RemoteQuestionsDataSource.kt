package it.reply.open.trimoji.data.remote

import com.mohamedrejeb.ksoup.entities.KsoupEntities
import io.ktor.client.HttpClient
import it.reply.open.trimoji.data.QuestionsDataSource
import it.reply.open.trimoji.data.model.OpenTDBException
import it.reply.open.trimoji.data.model.OpenTDBQuestionsResponse
import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.remote.util.ApiResult
import it.reply.open.trimoji.data.remote.util.map
import it.reply.open.trimoji.data.remote.util.safeGet
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class RemoteQuestionsDataSource(
    private val httpClient: HttpClient,
): QuestionsDataSource {

    companion object {
        val AWAIT_BETWEEN_REQUESTS = 1.seconds
    }

    private var lastRequest: Instant? = null
    private val lastRequestMutex = Mutex()

    override suspend fun getTriviaQuestions(amount: Int): ApiResult<List<Question>> = lastRequestMutex.withLock {
        val lastRequest = lastRequest
        if(lastRequest != null){
            val elapsedSinceLastRequest = Clock.System.now() - lastRequest
            delay(AWAIT_BETWEEN_REQUESTS - elapsedSinceLastRequest)
        }

        this.lastRequest = Clock.System.now()
        httpClient
            .safeGet<OpenTDBQuestionsResponse>(
                urlString = """https://opentdb.com/api.php?amount=${amount}&difficulty=easy"""
            )
            .map { resp ->
                when (val code = resp.responseCodeEnum) {
                    OpenTDBQuestionsResponse.OpenTDBResponseCode.Success -> {
                        /*continue*/
                    }

                    OpenTDBQuestionsResponse.OpenTDBResponseCode.NoResults,
                    OpenTDBQuestionsResponse.OpenTDBResponseCode.InvalidParameter,
                    OpenTDBQuestionsResponse.OpenTDBResponseCode.TokenNotFound,
                    OpenTDBQuestionsResponse.OpenTDBResponseCode.TokenEmpty,
                    OpenTDBQuestionsResponse.OpenTDBResponseCode.RateLimit,
                        -> {
                        return@getTriviaQuestions ApiResult.OtherError(OpenTDBException(code))
                    }
                }
                resp.results.map { it.decode() }
            }
    }

    private fun Question.decode(): Question = copy(
        questionText = KsoupEntities.decodeHtml(questionText),
        correctAnswer = KsoupEntities.decodeHtml(correctAnswer),
        incorrectAnswers = incorrectAnswers.map { KsoupEntities.decodeHtml(it) }
    )

}