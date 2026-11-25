package com.example.mind_set.ui.screens.jurnal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JournalViewModel(
    private val repository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        observeJournals()
    }

    private fun observeJournals() {
        viewModelScope.launch {
            repository.getAllJournals()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage) }
                }
                .collect { list ->
                    _uiState.update { it.copy(journals = list) }
                }
        }
    }

    fun setTab(tab: JournalTab) {
        _uiState.update { it.copy(currentTab = tab, errorMessage = null, successMessage = null) }
    }

    fun setSelectedDay(dayIndex: Int) {
        _uiState.update { it.copy(selectedDay = dayIndex) }
    }

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun updateTriggerText(text: String) {
        _uiState.update { it.copy(triggerText = text) }
    }

    fun updateReframingText(text: String) {
        _uiState.update { it.copy(reframingText = text) }
    }

    fun toggleTag(tag: String) {
        _uiState.update { state ->
            val updatedTags = if (state.selectedTags.contains(tag)) {
                state.selectedTags - tag
            } else {
                state.selectedTags + tag
            }
            state.copy(selectedTags = updatedTags)
        }
    }

    fun saveJournal(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.triggerText.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tuliskan pemicu atau catatan perasaan terlebih dahulu.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.saveJournal(
                    mood = state.selectedMood,
                    triggerText = state.triggerText.trim(),
                    reframingText = state.reframingText.trim(),
                    tags = state.selectedTags.toList()
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        triggerText = "",
                        reframingText = "",
                        selectedTags = emptySet(),
                        successMessage = "Jurnal berhasil disimpan & dianalisis CBT AI!",
                        currentTab = JournalTab.HISTORY
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Gagal menyimpan jurnal."
                    )
                }
            }
        }
    }

    fun deleteJournal(id: Int) {
        viewModelScope.launch {
            try {
                repository.deleteJournal(id)
            } catch (_: Exception) {}
        }
    }

    companion object {
        fun provideFactory(repository: JournalRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
                        return JournalViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
