package it.reply.open.trimoji.di

import androidx.room.Room
import io.ktor.client.engine.darwin.Darwin
import it.reply.open.trimoji.data.local.TrimojiDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun Module.httpClientSingle() {
    single {
        createHttpClient(Darwin)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun Module.databaseBuilderSingle() {
    single {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )?.path?.let(::requireNotNull)

        val dbFilePath = "$documentDirectory/my_room.db"
        Room.databaseBuilder<TrimojiDatabase>(
            name = dbFilePath,
        )
    }
}

