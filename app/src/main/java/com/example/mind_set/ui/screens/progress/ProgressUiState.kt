package com.example.mind_set.ui.screens.progress

import com.example.mind_set.data.model.CalendarDay
import com.example.mind_set.data.model.ProgressSummary

data class ProgressUiState(
    val summary: ProgressSummary = ProgressSummary(),
    val calendarDays: List<CalendarDay> = emptyList(),
    val monthLabel: String = "",
    val canGoNextMonth: Boolean = false,
    val canGoPreviousMonth: Boolean = true,
    val showRelapseDialog: Boolean = false,
    val relapseTrigger: String = "",
    val relapseNote: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
