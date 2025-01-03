package it.reply.open.trimoji.data.remote

import com.mohamedrejeb.ksoup.entities.KsoupEntities
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import it.reply.open.trimoji.data.model.ApiException
import it.reply.open.trimoji.data.model.Question
import it.reply.open.trimoji.data.model.TriviaSetResponse
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

    override suspend fun getTriviaQuestions(amount: Int): List<Question> = lastRequestMutex.withLock {
        val lastRequest = lastRequest
        if(lastRequest != null){
            val elapsedSinceLastRequest = Clock.System.now() - lastRequest
            delay(AWAIT_BETWEEN_REQUESTS - elapsedSinceLastRequest)
        }

        this.lastRequest = Clock.System.now()
        httpClient
            .get("""https://opentdb.com/api.php?amount=${amount}&difficulty=easy""")
            .body<TriviaSetResponse>()
            .let { resp ->
                when (val code = resp.responseCodeEnum) {
                    TriviaSetResponse.OpenTDBResponseCode.Success -> {
                        /*continue*/
                    }

                    TriviaSetResponse.OpenTDBResponseCode.NoResults,
                    TriviaSetResponse.OpenTDBResponseCode.InvalidParameter,
                    TriviaSetResponse.OpenTDBResponseCode.TokenNotFound,
                    TriviaSetResponse.OpenTDBResponseCode.TokenEmpty,
                    TriviaSetResponse.OpenTDBResponseCode.RateLimit,
                        -> {
                        throw ApiException("Received ${code.name} as response code.")
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