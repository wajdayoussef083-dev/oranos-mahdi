package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        MemoryEntity::class,
        FavoriteItemEntity::class,
        TaskHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class OranosDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun memoryDao(): MemoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun taskHistoryDao(): TaskHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: OranosDatabase? = null

        fun getDatabase(context: Context): OranosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OranosDatabase::class.java,
                    "oranos_mahdi_ai.db"
                ).fallbackToDestructiveMigration(true)
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
