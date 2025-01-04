package it.reply.open.trimoji.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import it.reply.open.trimoji.BuildKonfig
import it.reply.open.trimoji.data.cache.MemCachedQuestionsDataSource
import it.reply.open.trimoji.data.remote.OpenAIDataSource
import it.reply.open.trimoji.data.remote.RemoteQuestionsDataSource
import it.reply.open.trimoji.data.repository.OpenAIRepository
import it.reply.open.trimoji.data.repository.TriviaRepository
import it.reply.open.trimoji.domain.GetTrimojiGameUseCase
import it.reply.open.trimoji.ui.screen.questions.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule: Module = module {
    single(TrimojiQualifier.Dispatcher.Default) {
        Dispatchers.Default
    }

    single(TrimojiQualifier.Dispatcher.IO) {
        Dispatchers.IO
    }

    single(TrimojiQualifier.Dispatcher.Main) {
        Dispatchers.Main
    }

    single {
        GetTrimojiGameUseCase(
            triviaRepository = get(),
            openAIRepository = get(),
            defaultDispatcher = get(TrimojiQualifier.Dispatcher.Default)
        )
    }
    viewModelOf(::GameViewModel)
}

val dataModule: Module = module {

    single {
        RemoteQuestionsDataSource(
            httpClient = get(),
        )
    }

    single {
        MemCachedQuestionsDataSource(
            remoteSource = get(),
            // Filtering out questions with "overwatch" because OpenTDB contains a LOT of stuff about overwatch
            blacklistWords = listOf("overwatch"),
        )
    }

    single {
        TriviaRepository(
            dataSource = get<MemCachedQuestionsDataSource>(),
            ioDispatcher = get(TrimojiQualifier.Dispatcher.IO),
        )
    }

    single {
        OpenAIDataSource(
            apiToken = BuildKonfig.OPENAI_API_KEY,
            httpClient = get(),
        )
    }

    single {
        OpenAIRepository(
            openAIDataSource = get(),
        )
    }
}

expect val networkingModule: Module


/**
 * Used by platform-dependant code to initialize Ktor client
 */
fun <C, F> createHttpClient(factory: F): HttpClient
        where C : HttpClientEngineConfig, F : HttpClientEngineFactory<C> {
    return HttpClient(factory) {
        install(HttpTimeout) {
            socketTimeoutMillis = 30_000
            requestTimeoutMillis = 15_000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.d("KtorClient") { message }
                }
            }
        }
        defaultRequest {
            header("Content-Type", "application/json")

        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
    }
}