package com.example.mind_set.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mind_set.data.local.entity.RelapseLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelapseLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelapse(entity: RelapseLogEntity): Long

    @Query("SELECT * FROM relapse_logs WHERE username = :username ORDER BY created_at DESC")
    fun getAllRelapses(username: String): Flow<List<RelapseLogEntity>>

    @Query("DELETE FROM relapse_logs WHERE id = :id")
    suspend fun deleteRelapse(id: Int)
}
