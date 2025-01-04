package it.reply.open.trimoji.ui.screen.results

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.reply.open.trimoji.ui.designsystem.TrimojiColors
import it.reply.open.trimoji.ui.designsystem.TrimojiShapedTopBar


@Composable
fun ResultsScreen(
    correctAnswers: Int,
    questions: Int,
    onBack: () -> Unit,
) {

    val doShare = rememberShareAction()

    Scaffold(
        topBar = {
            TrimojiShapedTopBar(
                onCloseRequest = {
                    onBack()
                },
            )
        },
        backgroundColor = TrimojiColors.mainViolet,
        contentColor = Color.White,
        content = { scaffoldPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
            ) {
                Text(
                    text = "$correctAnswers / $questions",
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.size(20.dp))
                Text(
                    text = when {
                        correctAnswers <= 0 -> "Well, at least you tried. \uD83D\uDE05"
                        correctAnswers < 5 -> "Nice try! \uD83D\uDE04"
                        correctAnswers < 10 -> "Good job, you're on fire! \uD83D\uDD25"
                        correctAnswers == 10 -> "PERFECT! \uD83E\uDD29"
                        else -> "Wait... did you hack something? \uD83E\uDDD1\u200D\uD83D\uDCBB"
                    },
                    fontStyle = FontStyle.Italic,
                )
            }
        },
        bottomBar = {
            BottomAppBar(
                backgroundColor = TrimojiColors.mainViolet,
                elevation = 0.dp,
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = {
                            doShare("Hey, I got $correctAnswers correct answers on Trimoji!")
                        }
                    ) {
                        Text(
                            text = "Share",
                            color = TrimojiColors.mainGold,
                        )
                    }
                }
            }
        }
    )
}

@Composable
expect fun rememberShareAction(): (String) -> Unit