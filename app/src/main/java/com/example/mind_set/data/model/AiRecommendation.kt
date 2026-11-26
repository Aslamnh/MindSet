package com.example.mind_set.data.model

data class AiRecommendation(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val activityMethod: String = "",
    val durationLabel: String = "5 Menit",
    val actionSteps: List<String> = emptyList(),
    val habitAvoidanceTip: String = "",
    val warningMessage: String? = null,
    val isHighRiskPattern: Boolean = false,
    val sourceType: String = "LOCAL_FALLBACK",
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
