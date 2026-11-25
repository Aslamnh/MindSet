package com.example.mind_set.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mind_set.data.local.entity.EmergencyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: EmergencyLogEntity): Long

    @Query("SELECT * FROM emergency_logs ORDER BY created_at DESC")
    fun getAllLogs(): Flow<List<EmergencyLogEntity>>

    @Query("SELECT * FROM emergency_logs WHERE is_synced = 0 ORDER BY created_at ASC")
    suspend fun getUnsyncedLogs(): List<EmergencyLogEntity>

    @Query("UPDATE emergency_logs SET is_synced = 1 WHERE id = :logId")
    suspend fun markAsSynced(logId: Int)

    @Query("UPDATE emergency_logs SET is_session_completed = 1 WHERE id = :logId")
    suspend fun markSessionCompleted(logId: Int)

    @Query("UPDATE emergency_logs SET additional_note = :note, is_session_completed = 1 WHERE id = :logId")
    suspend fun finalizeWithReflection(logId: Int, note: String)
}
