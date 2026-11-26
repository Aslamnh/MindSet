package com.example.mind_set.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mind_set.data.repository.AiRecommendationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val recommendationRepository: AiRecommendationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRecommendation()
        observeStats()
    }

    private fun observeRecommendation() {
        viewModelScope.launch {
            recommendationRepository.getLatestRecommendation()
                .catch { error ->
                    _uiState.update { it.copy(statusNotice = error.localizedMessage) }
                }
                .collect { rec ->
                    if (rec != null) {
                        _uiState.update { it.copy(recommendation = rec) }
                    } else {
                        refreshRecommendation()
                    }
                }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            recommendationRepository.getRecoveryStats()
                .catch { }
                .collect { stats ->
                    _uiState.update { it.copy(recoveryStats = stats) }
                }
        }
    }

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
        refreshRecommendation()
    }

    fun refreshRecommendation() {
        val currentMood = _uiState.value.selectedMood.ifBlank { "Tenang" }
        _uiState.update { it.copy(isLoadingRecommendation = true, statusNotice = null) }

        viewModelScope.launch {
            try {
                val result = recommendationRepository.generateAndSaveRecommendation(currentMood)
                _uiState.update {
                    it.copy(
                        isLoadingRecommendation = false,
                        recommendation = result.recommendation,
                        statusNotice = result.apiErrorNotice
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingRecommendation = false,
                        statusNotice = e.localizedMessage ?: "Gagal memperbarui rekomendasi."
                    )
                }
            }
        }
    }

    fun toggleBookmark() {
        val currentRec = _uiState.value.recommendation ?: return
        val newBookmarkState = !currentRec.isBookmarked

        _uiState.update {
            it.copy(recommendation = currentRec.copy(isBookmarked = newBookmarkState))
        }

        viewModelScope.launch {
            try {
                recommendationRepository.toggleBookmark(currentRec.id, newBookmarkState)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(recommendation = currentRec)
                }
            }
        }
    }

    fun setExerciseDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showExerciseDialog = visible) }
    }

    companion object {
        fun provideFactory(
            repository: AiRecommendationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                    return HomeViewModel(repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
