package com.example.mind_set.ui.screens.home

import com.example.mind_set.data.model.AiRecommendation
import com.example.mind_set.data.repository.RecoveryStats

data class HomeUiState(
    val selectedMood: String = "",
    val recoveryStats: RecoveryStats = RecoveryStats(),
    val recommendation: AiRecommendation? = null,
    val isLoadingRecommendation: Boolean = false,
    val statusNotice: String? = null,
    val showExerciseDialog: Boolean = false
)
