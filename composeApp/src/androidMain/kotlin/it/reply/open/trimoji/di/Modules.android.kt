package it.reply.open.trimoji.di

import android.content.Context
import androidx.room.Room
import io.ktor.client.engine.okhttp.OkHttp
import it.reply.open.trimoji.data.local.TrimojiDatabase
import org.koin.core.module.Module

actual fun Module.httpClientSingle() {
    single {
        createHttpClient(OkHttp)
    }
}


actual fun Module.databaseBuilderSingle() {
    single {
        val appContext: Context = get<Context>().applicationContext

        Room.databaseBuilder<TrimojiDatabase>(
            context = appContext,
            name = appContext.getDatabasePath("trimoji.db").absolutePath
        )
    }
}


