package it.reply.open.trimoji.ui.util

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun MultiplatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}