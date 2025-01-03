package it.reply.open.trimoji

import androidx.compose.ui.window.ComposeUIViewController
import it.reply.open.trimoji.di.appModule
import it.reply.open.trimoji.di.dataModule
import it.reply.open.trimoji.di.networkingModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(
                appModule,
                networkingModule,
                dataModule,
            )
        }
    },
    content = {
        App()
    },
)