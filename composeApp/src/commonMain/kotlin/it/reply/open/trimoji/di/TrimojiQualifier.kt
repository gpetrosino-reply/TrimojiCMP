package it.reply.open.trimoji.di

import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue

interface TrimojiQualifier: Qualifier {
    override val value: QualifierValue get() = this::class.qualifiedName?:""

    interface Dispatcher: TrimojiQualifier {
        object Default: Dispatcher
        object IO: Dispatcher
        object Main: Dispatcher
    }

}