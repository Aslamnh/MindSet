package com.example.mind_set.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_recommendations")
data class AiRecommendationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "activity_method")
    val activityMethod: String,

    @ColumnInfo(name = "duration_label")
    val durationLabel: String,

    @ColumnInfo(name = "action_steps")
    val actionSteps: String, // delimited by " | "

    @ColumnInfo(name = "habit_avoidance_tip")
    val habitAvoidanceTip: String,

    @ColumnInfo(name = "warning_message")
    val warningMessage: String? = null,

    @ColumnInfo(name = "is_high_risk_pattern")
    val isHighRiskPattern: Boolean = false,

    @ColumnInfo(name = "source_type")
    val sourceType: String,

    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
