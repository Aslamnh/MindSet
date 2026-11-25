package com.example.mind_set.ui.emergency

import com.example.mind_set.data.model.EmergencyIntervention

data class EmergencyUiState(
    val urgeLevel: Int = 3,
    val selectedTrigger: String = "😔 Kesepian",
    val additionalNote: String = "",
    val isLoading: Boolean = false,
    val currentIntervention: EmergencyIntervention? = null,
    val emergencyHistory: List<EmergencyIntervention> = emptyList(),
    val retryCount: Int = 0,
    val errorMessage: String? = null
)