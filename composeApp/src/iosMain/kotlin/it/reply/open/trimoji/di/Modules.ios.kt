package it.reply.open.trimoji.di

import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val networkingModule: Module = module {
    single {
        createHttpClient(Darwin)
    }
}