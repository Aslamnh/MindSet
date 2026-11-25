package com.example.mind_set.ui.screens.jurnal

import com.example.mind_set.data.model.JournalItem

enum class JournalTab { FORM, HISTORY }

data class JournalUiState(
    val journals: List<JournalItem> = emptyList(),
    val currentTab: JournalTab = JournalTab.FORM,
    val selectedDay: Int = 3,
    val selectedMood: String = "Tenang",
    val triggerText: String = "",
    val reframingText: String = "",
    val selectedTags: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
