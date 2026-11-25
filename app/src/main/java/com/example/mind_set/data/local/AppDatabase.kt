package com.example.mind_set.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.entity.EmergencyLogEntity

@Database(
    entities = [
        EmergencyLogEntity::class
        // Nanti tinggal tambahkan: JournalEntity::class, StreakEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun emergencyLogDao(): EmergencyLogDao
    // Nanti tinggal tambahkan: abstract fun journalDao(): JournalDao

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