package com.example.mind_set.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mind_set.data.local.entity.DailyCheckInEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyCheckInDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCheckIn(entity: DailyCheckInEntity): Long

    @Query("SELECT * FROM daily_checkins WHERE username = :username ORDER BY date_key ASC")
    fun getAllCheckIns(username: String): Flow<List<DailyCheckInEntity>>

    @Query("SELECT COUNT(*) FROM daily_checkins WHERE date_key = :dateKey AND username = :username")
    suspend fun countForDate(dateKey: String, username: String): Int
}
