package it.reply.open.trimoji.domain

import it.reply.open.trimoji.data.repository.OpenAIRepository
import it.reply.open.trimoji.data.repository.TriviaRepository
import it.reply.open.trimoji.util.singleValueFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration.Companion.seconds

/**
 * Takes care of creating a set of questions, where each question has its answer choices shuffled, and the text is
 * (in async) converted into Emojis.
 */
class GetQuestionSetUseCase(
    private val triviaRepository: TriviaRepository,
    private val openAIRepository: OpenAIRepository,
    private val defaultDispatcher: CoroutineDispatcher,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<TrimojiQuestion>> = singleValueFlow {
        triviaRepository.retrieveQuestionSet()
            .map { question ->
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

                    val convertedText = openAIRepository.convertToEmoji(question.plainText)

                    emit(question.withEmojiText(convertedText))
                }
            }
    }.flatMapLatest { questionList ->
        combine(questionList) { it.toList() }
    }.flowOn(defaultDispatcher)


}
