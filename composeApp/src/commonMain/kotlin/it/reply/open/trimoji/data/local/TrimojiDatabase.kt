package it.reply.open.trimoji.data.local

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Database(
    entities = [
        UserEntity::class,
    ],
    version = 1,
)
@ConstructedBy(TrimojiDatabaseConstructor::class)
abstract class TrimojiDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TrimojiDatabaseConstructor : RoomDatabaseConstructor<TrimojiDatabase> {
    override fun initialize(): TrimojiDatabase
}




@Dao
interface UserDao {
    @Insert
    suspend fun insert(item: UserEntity)

    @Update
    suspend fun update(item: UserEntity)


    @Query("SELECT * FROM UserEntity")
    fun getAllAsFlow(): Flow<List<UserEntity>>
}




@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String = "",
)