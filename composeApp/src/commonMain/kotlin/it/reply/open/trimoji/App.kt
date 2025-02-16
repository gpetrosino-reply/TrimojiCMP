package it.reply.open.trimoji

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import it.reply.open.trimoji.navigation.TrimojiNavHost
import it.reply.open.trimoji.ui.designsystem.TrimojiColors
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            ColoredSystemBars {
                TrimojiNavHost()
            }
        }
    }
}

@Composable
private fun ColoredSystemBars(
    appContent: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.background(Color.White)
                    .fillMaxWidth()
                    .statusBarsPadding()
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.background(TrimojiColors.mainViolet)
                    .fillMaxWidth()
                    .navigationBarsPadding()
            )
        }
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(scaffoldPadding)
        ) {
            appContent()
        }
    }
}