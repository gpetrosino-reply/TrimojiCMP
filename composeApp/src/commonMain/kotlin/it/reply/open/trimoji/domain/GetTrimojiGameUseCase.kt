package it.reply.open.trimoji.domain

import it.reply.open.trimoji.data.remote.util.ApiResult
import it.reply.open.trimoji.data.remote.util.map
import it.reply.open.trimoji.data.repository.OpenAIRepository
import it.reply.open.trimoji.data.repository.TriviaRepository
import it.reply.open.trimoji.util.singleValueFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.seconds

/**
 * Takes care of creating a set of questions, where each question has its answer choices shuffled, and the text is
 * (in async) converted into Emojis.
 */
class GetTrimojiGameUseCase(
    private val triviaRepository: TriviaRepository,
    private val openAIRepository: OpenAIRepository,
    private val defaultDispatcher: CoroutineDispatcher,
) {
    operator fun invoke(amount: Int): Flow<TrimojiGame> = singleValueFlow {
        triviaRepository.retrieveQuestionSet(amount).map { questions  ->
            questions.map { question ->
                val incorrectAnswers = question.incorrectAnswers.map {
                    TrimojiAnswer(
                        text = it,
                        isCorrect = false
                    )
                }

                val correctAnswer = TrimojiAnswer(
                    text = question.correctAnswer,
                    isCorrect = true
                )

                TrimojiQuestion.Unconverted(
                    plainText = question.questionText,
                    answers = (incorrectAnswers + correctAnswer).shuffled(),
                )
            }.mapIndexed { i, question ->
                flow {
                    emit(question)

                    delay(i.seconds) // We wait 1 sec between requests to openAI to avoid 429s

                    when(val convertedTextResult = openAIRepository.convertToEmoji(question.plainText)) {
                        is ApiResult.NetworkError -> {
                            emit(question.couldNotConvert(noNetwork = true))
                        }
                        is ApiResult.ApiFailure -> {
                            emit(question.couldNotConvert(noNetwork = false))
                        }
                        is ApiResult.Success -> {
                            emit(question.withEmojiText(convertedTextResult.value))
                        }
                    }
                }.flowOn(defaultDispatcher)
            }
        }

    }.map { questionListResult: ApiResult<List<Flow<TrimojiQuestion>>> ->
        when(questionListResult) {
            is ApiResult.NetworkError -> {
                TrimojiGame.Unavailable(noNetwork = true)
            }
            is ApiResult.ApiFailure -> {
                TrimojiGame.Unavailable(noNetwork = false)
            }
            is ApiResult.Success -> {
                TrimojiGame.QuestionSet(questionListResult.value)
            }
        }

    }.flowOn(defaultDispatcher)


}
