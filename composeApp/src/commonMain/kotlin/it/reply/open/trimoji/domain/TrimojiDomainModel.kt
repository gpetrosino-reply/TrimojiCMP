package it.reply.open.trimoji.domain

import kotlinx.coroutines.flow.Flow

data class TrimojiAnswer(
    val isCorrect: Boolean,
    val text: String,
)

sealed interface TrimojiGame {
    data class Unavailable(
        val noNetwork: Boolean,
    ): TrimojiGame

    data class QuestionSet(
        val questions: List<Flow<TrimojiQuestion>>,
    ): TrimojiGame
}

sealed interface TrimojiQuestion {
    val plainText: String
    val answers: List<TrimojiAnswer>

    data class Unconverted(
        override val plainText: String,
        override val answers: List<TrimojiAnswer>,
    ) : TrimojiQuestion {
        fun withEmojiText(emojiText: String) = Ready(
            plainText = plainText,
            answers = answers,
            emojiText = emojiText,
        )

        fun couldNotConvert(noNetwork: Boolean) = Error(
            noNetwork = noNetwork,
            plainText = plainText,
            answers = answers,
        )
    }

    data class Error(
        val noNetwork: Boolean,
        override val plainText: String,
        override val answers: List<TrimojiAnswer>,
    ): TrimojiQuestion


    sealed interface WithEmojiText : TrimojiQuestion {
        val emojiText: String
    }

    data class Ready(
        override val plainText: String,
        override val answers: List<TrimojiAnswer>,
        override val emojiText: String,
    ) : WithEmojiText {
        fun provideAnswer(givenAnswer: Int) = Answered(
            plainText = plainText,
            answers = answers,
            emojiText = emojiText,
            givenAnswer = givenAnswer
        )
    }


    data class Answered(
        override val plainText: String,
        override val answers: List<TrimojiAnswer>,
        override val emojiText: String,
        val givenAnswer: Int,
    ) : WithEmojiText
}


fun TrimojiQuestion.isCorrect(): Boolean? {
    return when (this) {
        is TrimojiQuestion.Error,
        is TrimojiQuestion.Unconverted,
        is TrimojiQuestion.Ready -> null
        is TrimojiQuestion.Answered -> {
            answers.getOrNull(givenAnswer)?.isCorrect
        }
    }
}