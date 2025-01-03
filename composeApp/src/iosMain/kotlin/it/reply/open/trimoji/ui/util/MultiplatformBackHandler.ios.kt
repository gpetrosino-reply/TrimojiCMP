package it.reply.open.trimoji.ui.util

import androidx.compose.runtime.Composable

@Composable
actual fun MultiplatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    //Do nothing. No "system back gesture" available on iOS.
}