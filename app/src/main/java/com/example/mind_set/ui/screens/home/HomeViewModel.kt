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
    private val aiRecommendationRepository: AiRecommendationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeRecoveryStats()
        observeLatestRecommendation()
    }

    private fun observeRecoveryStats() {
        viewModelScope.launch {
            aiRecommendationRepository.getRecoveryStats()
                .catch { /* keep default stats */ }
                .collect { stats ->
                    _uiState.update { it.copy(recoveryStats = stats) }
                }
        }
    }

    private fun observeLatestRecommendation() {
        viewModelScope.launch {
            aiRecommendationRepository.getLatestRecommendation()
                .catch { /* ignore */ }
                .collect { latest ->
                    if (latest != null) {
                        _uiState.update { it.copy(recommendation = latest) }
                    } else if (!_uiState.value.isLoadingRecommendation) {
                        refreshRecommendation()
                    }
                }
        }
    }

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
        refreshRecommendation(moodOverride = mood)
    }

    fun refreshRecommendation(moodOverride: String? = null) {
        val mood = moodOverride ?: _uiState.value.selectedMood
        _uiState.update { it.copy(isLoadingRecommendation = true, statusNotice = null) }

        viewModelScope.launch {
            try {
                val result = aiRecommendationRepository.generateAndSaveRecommendation(mood)
                _uiState.update {
                    it.copy(
                        recommendation = result.recommendation,
                        isLoadingRecommendation = false,
                        statusNotice = result.apiErrorNotice
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingRecommendation = false,
                        statusNotice = e.localizedMessage ?: "Gagal memuat rekomendasi AI."
                    )
                }
            }
        }
    }

    fun toggleBookmark() {
        val current = _uiState.value.recommendation ?: return
        if (current.id == 0) return
        viewModelScope.launch {
            aiRecommendationRepository.toggleBookmark(current.id, !current.isBookmarked)
        }
    }

    fun setExerciseDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showExerciseDialog = visible) }
    }

    companion object {
        fun provideFactory(
            aiRecommendationRepository: AiRecommendationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(aiRecommendationRepository) as T
            }
        }
    }
}
