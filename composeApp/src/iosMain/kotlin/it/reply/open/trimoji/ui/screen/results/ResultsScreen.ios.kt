package it.reply.open.trimoji.ui.screen.results

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.UIKit.popoverPresentationController
import platform.UIKit.setModalInPresentation


@Composable
actual fun rememberShareAction(): (String) -> Unit = remember {
    action@{ textToShare ->
        val activityController = UIActivityViewController(
            activityItems = listOf(textToShare),
            applicationActivities = null
        )
        val window = UIApplication.sharedApplication.windows().firstOrNull() as? UIWindow? ?: return@action
        activityController.popoverPresentationController()?.sourceView = window
        activityController.setModalInPresentation(true)
        window.rootViewController?.presentViewController(
            activityController,
            animated = true,
            completion = null
        )
    }
}