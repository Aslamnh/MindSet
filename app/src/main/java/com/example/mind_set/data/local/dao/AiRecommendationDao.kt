package com.example.mind_set.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mind_set.data.local.entity.AiRecommendationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiRecommendationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommendation(entity: AiRecommendationEntity): Long

    @Query("SELECT * FROM ai_recommendations ORDER BY created_at DESC LIMIT 1")
    fun getLatestRecommendation(): Flow<AiRecommendationEntity?>

    @Query("SELECT * FROM ai_recommendations ORDER BY created_at DESC LIMIT 10")
    fun getRecentRecommendations(): Flow<List<AiRecommendationEntity>>

    @Query("SELECT * FROM ai_recommendations WHERE is_bookmarked = 1 ORDER BY created_at DESC")
    fun getBookmarkedRecommendations(): Flow<List<AiRecommendationEntity>>

    @Query("UPDATE ai_recommendations SET is_bookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Int, isBookmarked: Boolean)
}
