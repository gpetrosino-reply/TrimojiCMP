package it.reply.open.trimoji.ui.util

import androidx.compose.runtime.Composable

/**
 * Multiplatform version of android's BackHandler.
 * Since in iOS there is no "System-wide back gesture / button", then the iOS actual declaration is empty.
 * The Android actual declaration delegates to BackHandler.
 */
@Composable
expect fun MultiplatformBackHandler(
    enabled: Boolean = true,
    onBack: () -> Unit
)