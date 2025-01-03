package it.reply.open.trimoji.ui.screen.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
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
    Surface(
        color = TrimojiColors.mainGold,
        modifier = Modifier.fillMaxSize()
    ){

    }
}