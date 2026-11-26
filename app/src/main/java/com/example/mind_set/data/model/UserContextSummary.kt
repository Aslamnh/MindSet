package com.example.mind_set.data.model

data class UserContextSummary(
    val currentMood: String = "",
    val currentStreakDays: Int = 1,
    val successRatioPercent: Double = 100.0,
    val totalJournals: Int = 0,
    val totalEmergencySessions: Int = 0,
    val dominantTriggers: List<String> = emptyList(),
    val recentJournalSummary: String = "",
    val recentEmergencySummary: String = ""
)
