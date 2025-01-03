package it.reply.open.trimoji.domain

data class TrimojiAnswer(
    val isCorrect: Boolean,
    val text: String,
)

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
            emojiText = emojiText
        )
    }


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