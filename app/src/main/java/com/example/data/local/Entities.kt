package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdatedAt: Long = System.currentTimeMillis(),
    val languageMode: String = "ARABIC"
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val sender: String, // "user" or "oranos"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolType: String? = null,
    val isFavorite: Boolean = false
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "تفضيل", "مشروع", "هدف", "معلومة عامة"
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "دردشة", "مهمة", "استراتيجية"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceRef: String? = null
)

@Entity(tableName = "task_history")
data class TaskHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskType: String,
    val taskTitle: String,
    val inputSummary: String,
    val resultText: String,
    val timestamp: Long = System.currentTimeMillis()
)
