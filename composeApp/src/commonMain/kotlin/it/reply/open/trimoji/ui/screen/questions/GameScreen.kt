package it.reply.open.trimoji.ui.screen.questions

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.reply.open.trimoji.ui.designsystem.TrimojiColors
import it.reply.open.trimoji.ui.designsystem.TrimojiIconButton
import it.reply.open.trimoji.ui.designsystem.TrimojiProgressBar
import it.reply.open.trimoji.ui.designsystem.TrimojiTopBar
import it.reply.open.trimoji.ui.screen.loading.LoadingScreen
import it.reply.open.trimoji.ui.screen.questions.GameUIState.AnswerState.Correct
import it.reply.open.trimoji.ui.screen.questions.GameUIState.AnswerState.Normal
import it.reply.open.trimoji.ui.screen.questions.GameUIState.AnswerState.Wrong
import it.reply.open.trimoji.ui.util.MultiplatformBackHandler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import trimoji.composeapp.generated.resources.Res
import trimoji.composeapp.generated.resources.ico_close
import trimoji.composeapp.generated.resources.ico_fwd

@Composable
fun GameScreen(
    vm: GameViewModel = koinViewModel(),
    onDone: (correctAnswersCount: Int) -> Unit,
    onAbort: () -> Unit,
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value

    var showCloseConfirmDialog by remember { mutableStateOf(false) }

    MultiplatformBackHandler {
        showCloseConfirmDialog = true
    }
    when (uiState) {
        is GameUIState.Loading -> {
            LoadingScreen()
        }


        is GameUIState.Done -> {
            onDone(uiState.correctCount)
        }

        is GameUIState.Error -> {
            GameErrorScaffold(
                errorMessage = uiState.msg,
                onCloseRequest = { showCloseConfirmDialog = true },
            )
        }

        is GameUIState.Pages -> {
            GamePagesScaffold(
                uiState = uiState,
                onAnswerClick = { questionPageIndex: Int, answerIndex: Int ->
                    vm.onAnswerClick(
                        questionPageIndex,
                        answerIndex
                    )
                },
                onNextClick = { vm.onNextClick() },
                onCloseRequest = { showCloseConfirmDialog = true },
            )
        }
    }


    if (showCloseConfirmDialog) {
        AlertDialog(
            onDismissRequest = {
                showCloseConfirmDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCloseConfirmDialog = false
                        onAbort()
                    },
                ) {
                    Text("✅")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCloseConfirmDialog = false
                    },
                ) {
                    Text("❌")
                }
            },
            title = {
                Text(
                    text = "\uD83D\uDE28 Are you sure you want to exit? ⁉\uFE0F"
                )
            }
        )
    }
}


@Composable
private fun GameScaffold(
    onCloseRequest: () -> Unit,
    bottomBar: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TrimojiTopBar(
                navigationIconImage = {
                    Image(
                        painter = painterResource(Res.drawable.ico_close),
                        contentDescription = "close",
                        modifier = Modifier
                            .padding(5.dp)
                    )
                },
                onCloseRequest = {
                    onCloseRequest()
                }
            )
        },
        content = { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .background(TrimojiColors.mainViolet)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 0.dp,
                                bottomEnd = 35.dp,
                                bottomStart = 35.dp,
                            )
                        )
                        .background(Color.White)
                ) {
                    content()
                }
            }
        },
        bottomBar = {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(TrimojiColors.mainViolet)
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                bottomBar()
            }
        }
    )
}

@Composable
private fun GamePagesScaffold(
    uiState: GameUIState.Pages,
    onAnswerClick: (questionPageIndex: Int, selectedAnswer: Int) -> Unit,
    onNextClick: () -> Unit,
    onCloseRequest: () -> Unit,
) {
    val pagerState = rememberPagerState { uiState.pages.size }

    LaunchedEffect(uiState.currentPage) {
        if (uiState.currentPage != pagerState.currentPage) {
            pagerState.animateScrollToPage(page = uiState.currentPage)
        }
    }

    GameScaffold(
        onCloseRequest = onCloseRequest,
        content = {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) { currentPageIndex ->
                when (val currentPage = uiState.pages.getOrNull(currentPageIndex)) {
                    is GameUIState.QuestionPage -> {
                        QuestionPage(
                            page = currentPage,
                            onAnswerClick = { selectedAnswer ->
                                onAnswerClick(currentPageIndex, selectedAnswer)
                            },
                        )
                    }

                    null -> {
                        ErrorContent(
                            errorMessage = "Cannot find question with index $currentPageIndex in question set " +
                                    "with size ${uiState.pages.size}"
                        )
                    }
                }
            }

        },
        bottomBar = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            ) {
                PagesProgressIndicators(
                    index = pagerState.currentPage,
                    pagesCount = pagerState.pageCount,
                    modifier = Modifier.weight(.7f)
                )
                Spacer(Modifier.weight(.3f))
                Spacer(Modifier.size(10.dp))
                TrimojiIconButton(
                    enabled = uiState.pages.getOrNull(pagerState.currentPage)?.givenAnswer != null,
                    onClick = onNextClick,
                ) {
                    Image(
                        imageVector = vectorResource(Res.drawable.ico_fwd),
                        contentDescription = "next",
                        modifier = Modifier
                            .size(64.dp)
                            .padding(15.dp)
                    )
                }
            }

        }
    )
}

@Composable
private fun GameErrorScaffold(
    errorMessage: String? = null,
    onCloseRequest: () -> Unit,
) {
    GameScaffold(
        onCloseRequest = onCloseRequest,
        content = {
            ErrorContent(errorMessage)
        },
        bottomBar = {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(114.dp)
            )
        },
    )
}


@Composable
private fun QuestionPage(
    page: GameUIState.QuestionPage,
    onAnswerClick: (selectedAnswer: Int) -> Unit,
) {
    val answered = page.givenAnswer != null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val textEmoji = page.questionEmojiText
        if (textEmoji == null) {
            CircularProgressIndicator(
                color = TrimojiColors.mainViolet,
                modifier = Modifier
                    .height(48.dp)
            )
        } else {
            Text(
                text = textEmoji + if (answered) {
                    "\n\n${page.questionPlainText}"
                } else {
                    ""
                },
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .animateContentSize()
            )
        }
        Spacer(modifier = Modifier.size(30.dp))
        for ((i, answer) in page.answers.withIndex()) {
            AnswerCard(
                answer = answer,
                enabled = page.givenAnswer == null,
                selected = page.givenAnswer == i,
                onAnswerClick = { onAnswerClick(i) }
            )
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}


@Composable
private fun AnswerCard(
    enabled: Boolean,
    selected: Boolean,
    answer: GameUIState.Answer,
    onAnswerClick: () -> Unit,
) {
    val answerShape = remember { RoundedCornerShape(25.dp) }
    Button(
        shape = answerShape,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = if (selected) {
                TrimojiColors.mainViolet
            } else {
                Color.White
            }
        ),
        enabled = enabled,
        onClick = {
            onAnswerClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .border(width = 2.dp, color = Color.Gray, shape = answerShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp)
                .fillMaxWidth()
        ) {
            AnswerCardCircle(
                circleColor = when (answer.answerState) {
                    Normal -> Color.Transparent
                    Correct -> Color.Green
                    Wrong -> Color.Red
                },
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.size(25.dp))
            Text(
                text = answer.text,
                color = if (selected) {
                    Color.White
                } else {
                    Color.Black
                }
            )
        }
    }
}


@Composable
private fun AnswerCardCircle(
    circleColor: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier,
    ) {
        drawCircle(
            color = Color.White,
        )
        drawCircle(
            color = TrimojiColors.mainGrey,
            style = Stroke(width = 2f),
        )
        drawCircle(
            color = circleColor,
            radius = size.minDimension * 0.4f
        )
    }
}

@Composable
private fun PagesProgressIndicators(
    index: Int,
    pagesCount: Int,
    modifier: Modifier = Modifier,
) {
    val progress = (index + 1).toFloat() / (pagesCount + 1).toFloat()
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Question ${index + 1} of $pagesCount",
            fontWeight = FontWeight.Medium,
            color = TrimojiColors.mainGrey,
        )
        Spacer(Modifier.size(10.dp))
        TrimojiProgressBar(
            progress = progress,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp)
    ) {
        Text(
            text = "⚠\uFE0F⚠\uFE0F⚠\uFE0F",
            fontSize = 40.sp,
        )
        Spacer(modifier = Modifier.size(15.dp))
        Text(
            text = "An Error occurred...",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        if (errorMessage != null) {
            Spacer(modifier = Modifier.size(15.dp))
            Text(
                text = errorMessage,
                softWrap = true
            )
        }
    }
}


