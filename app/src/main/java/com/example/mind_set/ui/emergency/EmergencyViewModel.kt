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
    private val maxRetryBeforeReflection = 3

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

    fun updateReflectionText(text: String) {
        _uiState.update { it.copy(reflectionText = text) }
    }

    // ── Phase 1: Submit assessment awal & dapatkan panduan CBT ──────────
    fun submitEmergencyAssessment() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isSessionCompleted) return

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
                        currentIntervention = result,
                        finalLogId = result.id
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

    // ── Phase 1 (retry): Rotasi panduan CBT alternatif ──────────────────
    // Jika retryCount < 3: putar panduan generik berikutnya.
    // Jika retryCount >= 3: transisi ke Phase 2 (Form Refleksi Detail).
    fun retryWithAlternativeGuidance() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isSessionCompleted) return

        val nextRetryCount = currentState.retryCount + 1

        // Sudah 3x retry → masuk fase Refleksi Detail Krisis
        if (nextRetryCount >= maxRetryBeforeReflection) {
            _uiState.update {
                it.copy(
                    retryCount = nextRetryCount,
                    isReflectionPhase = true
                )
            }
            return
        }

        // Masih di bawah 3x → ambil panduan alternatif
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
                val baseNote = currentState.additionalNote.trim()
                val retryNote = if (baseNote.isNotBlank()) {
                    "$baseNote (Percobaan alternatif ke-$nextRetryCount)"
                } else {
                    "Eskalasi percobaan ke-$nextRetryCount"
                }

                val newResult = repository.processEmergencyAssessment(
                    urgeLevel = escalatedUrgeLevel,
                    triggerCategory = currentState.selectedTrigger,
                    additionalNote = retryNote,
                    excludeTitle = previousTitle,
                    retryCount = nextRetryCount
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentIntervention = newResult,
                        retryCount = nextRetryCount,
                        finalLogId = newResult.id
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

    // ── Phase 2 → 3: Kirim refleksi detail & tutup sesi ────────────────
    fun submitReflectionAndClose() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isSessionCompleted) return
        if (currentState.reflectionText.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Ceritakan sedikit tentang apa yang sedang kamu rasakan...")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val trimmedReflection = currentState.reflectionText.trim()
                val logId = repository.submitReflectionAndClose(
                    urgeLevel = currentState.urgeLevel,
                    triggerCategory = currentState.selectedTrigger,
                    reflectionNote = trimmedReflection,
                    existingLogId = currentState.finalLogId
                )

                val closingMessage = repository.generatePersonalClosing(
                    urgeLevel = currentState.urgeLevel,
                    triggerCategory = currentState.selectedTrigger,
                    reflectionNote = trimmedReflection
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSessionCompleted = true,
                        closingMessage = closingMessage,
                        finalLogId = logId
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage
                            ?: "Gagal menyimpan refleksi. Coba lagi."
                    )
                }
            }
        }
    }

    // ── Tombol "Saya Berhasil" (shortcut close tanpa refleksi) ──────────
    fun markSessionSuccess() {
        val currentState = _uiState.value
        if (currentState.isSessionCompleted) return

        viewModelScope.launch {
            try {
                if (currentState.finalLogId != null) {
                    repository.submitReflectionAndClose(
                        urgeLevel = currentState.urgeLevel,
                        triggerCategory = currentState.selectedTrigger,
                        reflectionNote = "Pengguna berhasil melewati dorongan.",
                        existingLogId = currentState.finalLogId
                    )
                }
            } catch (_: Exception) {
                // Tetap tutup sesi meskipun DB error
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
