package it.reply.open.trimoji.ui.screen.questions

import it.reply.open.trimoji.base.TrimojiViewModel
import it.reply.open.trimoji.domain.GetQuestionSetUseCase
import it.reply.open.trimoji.domain.TrimojiAnswer
import it.reply.open.trimoji.domain.TrimojiQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


class QuestionsViewModel(
    private val getQuestionSetUseCase: GetQuestionSetUseCase,
) : TrimojiViewModel<QuestionsUIState, QuestionsEffects>() {

    private val currentPageIndex = MutableStateFlow(0)


    private val givenAnswers = MutableStateFlow(mapOf<Int, Int>())

    private val trimojiQuestions: StateFlow<List<TrimojiQuestion>?> =
        getQuestionSetUseCase()
            .combine(givenAnswers) { questions, answersMap ->
                questions.mapIndexed { i, question ->
                    when (question) {
                        is TrimojiQuestion.Unconverted -> question
                        is TrimojiQuestion.Answered -> question
                        is TrimojiQuestion.Ready -> {
                            val answer = answersMap[i] ?: return@mapIndexed question
                            question.provideAnswer(answer)
                        }
                    }
                }
            }
            .stateInViewModelScope(null)


    private val pageStates: StateFlow<List<QuestionsUIState.QuestionPage>?> =
        trimojiQuestions.map { questions ->
            questions?.map { question -> question.convertIntoPage() }
        }.stateInViewModelScope(null)


    private val correctAnswersCount: Flow<Int> = trimojiQuestions.map { questionList ->
        questionList?.sumOf { question ->
            val returned: Int = when (question) {
                is TrimojiQuestion.Unconverted,
                is TrimojiQuestion.Ready,
                    -> 0

                is TrimojiQuestion.Answered -> {
                    val answers = question.answers
                    val providedAnswer = question.givenAnswer

                    if (answers.getOrNull(providedAnswer)?.isCorrect == true) 1 else 0
                }
            }
            returned
        } ?: 0
    }.stateInViewModelScope(0)


    override val uiState: StateFlow<QuestionsUIState> = combine(
        pageStates,
        currentPageIndex,
        correctAnswersCount,
    ) { pagesList, currentPageIndex, correctAnswersGiven ->
        pagesList ?: return@combine QuestionsUIState.Loading

        if (currentPageIndex < pagesList.size) {
            QuestionsUIState.Pages(
                pages = pagesList,
                currentPage = currentPageIndex,
                timer = 0.0f,
                timerMax = 0.0f,
            )
        } else {
            QuestionsUIState.Done(correctCount = correctAnswersGiven)
        }
    }.stateInViewModelScope(QuestionsUIState.Loading)

    fun onAnswerClick(
        questionPageIndex: Int,
        answerIndex: Int,
    ) {
        givenAnswers.update { it + (questionPageIndex to answerIndex) }
    }

    fun onNextClick() {
        currentPageIndex.update { it + 1 }
    }


    companion object {
        private fun TrimojiQuestion.convertIntoPage(): QuestionsUIState.QuestionPage {
            val providedAnswer = (this as? TrimojiQuestion.Answered)?.givenAnswer
            val answered = providedAnswer != null
            return QuestionsUIState.QuestionPage(
                questionPlainText = plainText,
                questionEmojiText = (this as? TrimojiQuestion.WithEmojiText)?.emojiText,
                answers = this.answers.mapIndexed { i, choice ->
                    choice.convertIntoChoiceButton(
                        answered = answered,
                        isSelected = providedAnswer == i
                    )
                },
                givenAnswer = providedAnswer,
            )
        }

        private fun TrimojiAnswer.convertIntoChoiceButton(
            answered: Boolean,
            isSelected: Boolean,
        ) = QuestionsUIState.Answer(
            text = text,
            answerState = when {
                answered && isCorrect -> QuestionsUIState.AnswerState.Correct
                answered && isSelected -> QuestionsUIState.AnswerState.Wrong
                else -> QuestionsUIState.AnswerState.Normal
            }
        )


    }

}

sealed interface QuestionsEffects

