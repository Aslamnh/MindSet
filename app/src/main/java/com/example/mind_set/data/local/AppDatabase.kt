package com.example.mind_set.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mind_set.data.local.dao.AiRecommendationDao
import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.dao.JournalDao
import com.example.mind_set.data.local.dao.UserDao
import com.example.mind_set.data.local.entity.AiRecommendationEntity
import com.example.mind_set.data.local.entity.EmergencyLogEntity
import com.example.mind_set.data.local.entity.JournalEntity
import com.example.mind_set.data.local.entity.UserEntity

@Database(
    entities = [
        EmergencyLogEntity::class,
        JournalEntity::class,
        UserEntity::class,
        AiRecommendationEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyLogDao(): EmergencyLogDao
    abstract fun journalDao(): JournalDao
    abstract fun userDao(): UserDao
    abstract fun aiRecommendationDao(): AiRecommendationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mindset_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
