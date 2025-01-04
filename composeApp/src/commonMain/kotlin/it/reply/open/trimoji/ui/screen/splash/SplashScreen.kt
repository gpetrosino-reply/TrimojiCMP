package it.reply.open.trimoji.ui.screen.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import it.reply.open.trimoji.ui.designsystem.TrimojiColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(
    onDoneLoading: () -> Unit
) {
    LaunchedEffect(Unit) {
        delay(2.seconds)
        onDoneLoading()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White,
                        TrimojiColors.mainViolet,
                    )
                )
            )
    )
}