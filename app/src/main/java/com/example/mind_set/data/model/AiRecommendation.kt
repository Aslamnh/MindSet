package com.example.mind_set.data.model

data class AiRecommendation(
    val id: Int = 0,
    val title: String,
    val description: String,
    val activityMethod: String,
    val durationLabel: String,
    val actionSteps: List<String>,
    val habitAvoidanceTip: String,
    val warningMessage: String? = null,
    val isHighRiskPattern: Boolean = false,
    val sourceType: String = "RULE_BASED_FALLBACK",
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserContextSummary(
    val currentMood: String,
    val currentStreakDays: Int,
    val successRatioPercent: Double,
    val totalJournals: Int,
    val totalEmergencySessions: Int,
    val dominantTriggers: List<String>,
    val recentJournalSummary: String,
    val recentEmergencySummary: String
)
