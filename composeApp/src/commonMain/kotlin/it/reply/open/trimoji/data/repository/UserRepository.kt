package it.reply.open.trimoji.data.repository

import it.reply.open.trimoji.data.local.TrimojiDatabase
import it.reply.open.trimoji.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val roomDatabase: TrimojiDatabase,
) {
    val currentUser: Flow<UserEntity?> = roomDatabase.getUserDao().getAllAsFlow()
        .map { it.firstOrNull() }


    suspend fun modifyUser(user: UserEntity) {
        roomDatabase.getUserDao().update(user)
    }
}