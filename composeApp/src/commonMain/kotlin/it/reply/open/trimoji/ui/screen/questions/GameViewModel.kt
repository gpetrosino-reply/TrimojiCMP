package it.reply.open.trimoji.ui.screen.questions

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import it.reply.open.trimoji.base.TrimojiViewModel
import it.reply.open.trimoji.domain.GetTrimojiGameUseCase
import it.reply.open.trimoji.domain.TrimojiGame
import it.reply.open.trimoji.domain.TrimojiAnswer
import it.reply.open.trimoji.domain.TrimojiQuestion
import it.reply.open.trimoji.domain.isCorrect
import it.reply.open.trimoji.navigation.TrimojiGraph
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update


@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(
    getTrimojiGameUseCase: GetTrimojiGameUseCase,
    savedStateHandle: SavedStateHandle,
) : TrimojiViewModel<GameUIState, GameEffects>() {

    private val questionAmount: Int = savedStateHandle.toRoute<TrimojiGraph.Game>().questionAmount

    private val currentPageIndex = MutableStateFlow(0)

    private val givenAnswers: List<MutableStateFlow<Int?>> = List(questionAmount) { MutableStateFlow(null) }

    private val trimojiGame: StateFlow<TrimojiGame?> =
        getTrimojiGameUseCase(questionAmount)
            .stateInViewModelScope(null)


    private val trimojiQuestionsFlows: StateFlow<List<Flow<TrimojiQuestion>>?> = trimojiGame.map { game ->
        game ?: return@map null

        val questionsFlows = when (game) {
            is TrimojiGame.Unavailable -> return@map null
            is TrimojiGame.QuestionSet -> {
                game.questions
            }
        }

        questionsFlows.zip(givenAnswers) { questionFlow, givenAnswerFlow ->
            questionFlow.combine(givenAnswerFlow) { question, answer ->
                answer ?: return@combine question
                when (question) {
                    is TrimojiQuestion.Unconverted,
                    is TrimojiQuestion.Error,
                    is TrimojiQuestion.Answered,
                        -> {
                        return@combine question
                    }

                    is TrimojiQuestion.Ready -> {
                        question.provideAnswer(answer)
                    }
                }
            }
        }
    }.stateInViewModelScope(null)


    private val correctAnswersCount: StateFlow<Int> = trimojiQuestionsFlows.flatMapLatest { questionsFlows ->
        questionsFlows ?: return@flatMapLatest flowOf(0)

        combine(questionsFlows) { questions ->
            questions.count { it.isCorrect() == true }
        }
    }.stateInViewModelScope(0)

    private val pageUIStates: StateFlow<List<GameUIState.QuestionPage>?> =
        trimojiQuestionsFlows.flatMapLatest { questionsFlows ->
            questionsFlows ?: return@flatMapLatest flowOf(null)
            val pagesFlows: List<Flow<GameUIState.QuestionPage>> = questionsFlows.map { questionFlow ->
                questionFlow.map {
                    it.convertIntoPage()
                }
            }

            combine(pagesFlows) { it.toList() }
        }.stateInViewModelScope(null)


    override val uiState: StateFlow<GameUIState> = combine(
        trimojiGame,
        pageUIStates,
        currentPageIndex,
        correctAnswersCount,
    ) { trimojiGame, pageStates, currentPageIndex, correctAnswersGiven ->


        when (trimojiGame) {
            is TrimojiGame.Unavailable -> {
                val msg = if(trimojiGame.noNetwork) {
                    """You are not connected to the internet! ❌📶"""
                }else{
                    """Something went wrong... 😨"""
                }
                return@combine GameUIState.Error(msg)
            }
            null -> {
                return@combine GameUIState.Loading
            }
            is TrimojiGame.QuestionSet -> {
                // continue
            }
        }

        pageStates ?: return@combine GameUIState.Loading

        if (currentPageIndex < pageStates.size) {
            GameUIState.Pages(
                pages = pageStates,
                currentPage = currentPageIndex,
                timer = 0.0f,
                timerMax = 0.0f,
            )
        } else {
            GameUIState.Done(correctCount = correctAnswersGiven)
        }
    }.stateInViewModelScope(GameUIState.Loading)

    fun onAnswerClick(
        questionPageIndex: Int,
        answerIndex: Int,
    ) = launchInViewModelScope {
        if(questionPageIndex !in givenAnswers.indices){
            throw IllegalArgumentException(
                "Could not update answer for question index out of bounds" +
                        ": $questionPageIndex (# of questions: ${givenAnswers.size})"
            )
        }

        givenAnswers[questionPageIndex].emit(answerIndex)
    }

    fun onNextClick() {
        currentPageIndex.update { it + 1 }
    }


    companion object {
        private fun TrimojiQuestion.convertIntoPage(): GameUIState.QuestionPage {
            val providedAnswer = (this as? TrimojiQuestion.Answered)?.givenAnswer
            val answered = providedAnswer != null
            return GameUIState.QuestionPage(
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
        ) = GameUIState.Answer(
            text = text,
            answerState = when {
                answered && isCorrect -> GameUIState.AnswerState.Correct
                answered && isSelected -> GameUIState.AnswerState.Wrong
                else -> GameUIState.AnswerState.Normal
            }
        )


    }

}

sealed interface GameEffects

