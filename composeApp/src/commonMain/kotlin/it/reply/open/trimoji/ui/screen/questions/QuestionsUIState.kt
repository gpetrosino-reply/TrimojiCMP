package it.reply.open.trimoji.ui.screen.questions


sealed interface QuestionsUIState {
    data object Loading: QuestionsUIState

    data class Error(val msg: String): QuestionsUIState

    data class Pages(
        val pages: List<QuestionPage>,
        val currentPage: Int,
        val timer: Float,
        val timerMax: Float,
    ): QuestionsUIState

    data class Done(
        val correctCount: Int,
    ): QuestionsUIState

    data class QuestionPage(
        val questionPlainText: String,
        val answers: List<Answer>,
        val questionEmojiText: String? = null,
        val givenAnswer: Int? = null,
    )

    data class Answer(
        val text: String,
        val answerState: AnswerState,
    )

    enum class AnswerState {
        Normal,
        Correct,
        Wrong,
    }
}