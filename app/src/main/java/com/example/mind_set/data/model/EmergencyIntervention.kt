package com.example.mind_set.data.model

data class EmergencyIntervention(
    val id: Int = 0,
    val urgeLevel: Int,
    val triggerCategory: String,
    val urgencyStatus: String,
    val cbtTechniqueTitle: String,
    val recommendationSteps: List<String>,
    val sourceType: String,
    val createdAt: Long = System.currentTimeMillis()
)