package it.reply.open.trimoji.ui.screen.questions


sealed interface GameUIState {
    data object Loading: GameUIState

    sealed interface ContentReady: GameUIState

    data class Error(val msg: String): ContentReady

    data class Pages(
        val pages: List<QuestionPage>,
        val currentPage: Int,
        val timer: Float,
        val timerMax: Float,
    ): ContentReady

    data class Done(
        val correctCount: Int,
    ): GameUIState

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