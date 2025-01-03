package it.reply.open.trimoji.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

fun <T1, T2> zip(f1: Flow<T1>, f2: Flow<T2>): Flow<Pair<T1, T2>> = combine(f1, f2, ::Pair)


fun <T> singleValueFlow(
    build: suspend () -> T
): Flow<T> {
    return flow { emit(build()) }
}