package it.reply.open.trimoji.ui.screen.results

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberShareAction(): (String) -> Unit {
    val context = LocalContext.current
    return remember {
        { textToShare ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }

            context.startActivity(Intent.createChooser(sendIntent, null))
        }
    }
}