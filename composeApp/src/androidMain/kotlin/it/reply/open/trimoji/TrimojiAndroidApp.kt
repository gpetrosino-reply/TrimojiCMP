package it.reply.open.trimoji

import android.app.Application
import it.reply.open.trimoji.di.appModule
import it.reply.open.trimoji.di.dataModule
import it.reply.open.trimoji.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class TrimojiAndroidApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            androidLogger()
            modules(
                appModule,
                networkingModule,
                dataModule,
            )
        }
    }

}