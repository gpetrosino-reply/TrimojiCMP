package it.reply.open.trimoji.ui.designsystem

import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import trimoji.composeapp.generated.resources.Res
import trimoji.composeapp.generated.resources.ico_back

@Composable
fun TrimojiIconButton(
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = remember { RoundedCornerShape(15.dp) }
    IconButton(
        onClick = onClick,
        enabled = enabled,
        content = content,
        modifier = Modifier
            .size(48.dp)
            .background(
                color = if (enabled) TrimojiColors.mainGold else TrimojiColors.mainGrey,
                shape = shape
            )
            .clip(shape)
            .then(modifier)
    )
}

@Composable
fun TrimojiProgressBar(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier,
        color = TrimojiColors.mainGold,
        backgroundColor = Color.White,
        strokeCap = StrokeCap.Round,
    )
}

@Composable
fun TrimojiTopBar(
    title: String = "Trimoji",
    navigationIconImage: @Composable () -> Unit = {
        Image(
            painter = painterResource(Res.drawable.ico_back),
            contentDescription = "back",
            modifier = Modifier
        )
    },
    onCloseRequest: () -> Unit,
) {
    TrimojiTopBarContent(
        title = title,
        navigationIconImage = navigationIconImage,
        onCloseRequest = onCloseRequest,
    )
}

@Composable
fun TopBarBackImage() {
    Image(
        painter = painterResource(Res.drawable.ico_back),
        contentDescription = "back",
        modifier = Modifier
            .background(Color.Transparent)
    )
}

@Composable
fun TrimojiRoundTopBar(
    title: String = "Trimoji",
    navigationIconImage: @Composable () -> Unit = { TopBarBackImage() },
    onCloseRequest: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(TrimojiColors.mainViolet)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 35.dp,
                        bottomEnd = 35.dp,
                    )
                )
        ) {
            TrimojiTopBarContent(
                title = title,
                navigationIconImage = navigationIconImage,
                onCloseRequest = onCloseRequest,
            )
        }
    }
}


@Composable
private fun TrimojiTopBarContent(
    title: String = "Trimoji",
    navigationIconImage: @Composable () -> Unit = { TopBarBackImage() },
    onCloseRequest: () -> Unit,
) {
    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = {
            Text(
                text = title,
                color = TrimojiColors.mainViolet,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onCloseRequest,
                modifier = Modifier
                    .size(48.dp)
                    .padding(horizontal = 10.dp)
            ) {
                navigationIconImage()
            }
        },
    )
}