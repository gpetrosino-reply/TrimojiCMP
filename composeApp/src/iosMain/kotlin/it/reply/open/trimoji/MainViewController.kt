package it.reply.open.trimoji

import androidx.compose.ui.window.ComposeUIViewController
import it.reply.open.trimoji.di.appModule
import it.reply.open.trimoji.di.dataModule
import org.koin.core.context.startKoin

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(
                appModule,
                dataModule,
            )
        }
    },
    content = {
        App()
    },
)