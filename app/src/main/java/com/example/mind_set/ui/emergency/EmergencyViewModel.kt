package com.example.mind_set.ui.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mind_set.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmergencyViewModel(
    private val repository: EmergencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        observeEmergencyHistory()
    }

    private fun observeEmergencyHistory() {
        viewModelScope.launch {
            repository.getEmergencyHistory()
                .catch { e ->
                    _uiState.update { it.copy(errorMessage = e.localizedMessage) }
                }
                .collect { historyList ->
                    _uiState.update { it.copy(emergencyHistory = historyList) }
                }
        }
    }

    fun updateUrgeLevel(level: Int) {
        _uiState.update { it.copy(urgeLevel = level.coerceIn(1, 5)) }
    }

    fun selectTrigger(trigger: String) {
        _uiState.update { it.copy(selectedTrigger = trigger) }
    }

    fun updateAdditionalNote(note: String) {
        _uiState.update { it.copy(additionalNote = note) }
    }

    fun submitEmergencyAssessment() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val result = repository.processEmergencyAssessment(
                    urgeLevel = currentState.urgeLevel,
                    triggerCategory = currentState.selectedTrigger,
                    additionalNote = currentState.additionalNote.trim()
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentIntervention = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Gagal memproses intervensi darurat."
                    )
                }
            }
        }
    }

    fun retryWithAlternativeGuidance() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val previousTitle = currentState.currentIntervention?.cbtTechniqueTitle
        val escalatedUrgeLevel = maxOf(currentState.urgeLevel, 4)

        _uiState.update {
            it.copy(
                isLoading = true,
                urgeLevel = escalatedUrgeLevel,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val newResult = repository.processEmergencyAssessment(
                    urgeLevel = escalatedUrgeLevel,
                    triggerCategory = currentState.selectedTrigger,
                    additionalNote = "Eskalasi percobaan ke-${currentState.retryCount + 1}",
                    excludeTitle = previousTitle
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentIntervention = newResult,
                        retryCount = it.retryCount + 1
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Gagal memuat panduan alternatif."
                    )
                }
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: EmergencyRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(EmergencyViewModel::class.java)) {
                    return EmergencyViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}