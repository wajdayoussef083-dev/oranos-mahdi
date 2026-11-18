package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ChatSessionEntity): Long

    @Update
    suspend fun updateSession(session: ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("SELECT * FROM chat_sessions ORDER BY lastUpdatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): ChatSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessagesSync(sessionId: Long, limit: Int = 20): List<ChatMessageEntity>

    @Query("UPDATE chat_messages SET isFavorite = :isFav WHERE id = :messageId")
    suspend fun updateMessageFavorite(messageId: Long, isFav: Boolean)

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: Long)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllSessions()

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()
}

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemory(id: Long)

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories ORDER BY createdAt DESC")
    suspend fun getAllMemoriesSync(): List<MemoryEntity>

    @Query("SELECT * FROM memories WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<MemoryEntity>>
}

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteItemEntity): Long

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteFavorite(id: Long)

    @Query("SELECT * FROM favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteItemEntity>>

    @Query("SELECT * FROM favorites WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchFavorites(query: String): Flow<List<FavoriteItemEntity>>
}

@Dao
interface TaskHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskHistory(task: TaskHistoryEntity): Long

    @Query("SELECT * FROM task_history WHERE taskType = :taskType ORDER BY timestamp DESC")
    fun getHistoryForTask(taskType: String): Flow<List<TaskHistoryEntity>>

    @Query("SELECT * FROM task_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllTaskHistory(): Flow<List<TaskHistoryEntity>>

    @Query("DELETE FROM task_history WHERE id = :id")
    suspend fun deleteTaskHistory(id: Long)

    @Query("DELETE FROM task_history WHERE taskType = :taskType")
    suspend fun clearHistoryForTask(taskType: String)
}
