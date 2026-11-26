package com.example.mind_set.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mind_set.data.model.CalendarDay
import com.example.mind_set.data.model.DayStatus
import com.example.mind_set.data.model.ProgressSummary
import com.example.mind_set.data.repository.ProgressRepository
import com.example.mind_set.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProgressViewModel(
    private val repository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private val selectedMonth =
        MutableStateFlow(DateUtils.currentYear() to DateUtils.currentMonth())

    init {
        observeProgress()
    }

    private fun observeProgress() {
        viewModelScope.launch {
            combine(repository.getProgress(), selectedMonth) { summary, yearMonth ->
                summary to yearMonth
            }
                .catch { error ->
                    _uiState.update { it.copy(errorMessage = error.localizedMessage) }
                }
                .collect { (summary, yearMonth) ->
                    val (year, month) = yearMonth
                    _uiState.update { current ->
                        current.copy(
                            summary = summary,
                            calendarDays = buildCalendar(summary, year, month),
                            monthLabel = DateUtils.monthLabel(year, month),
                            canGoNextMonth = hasNextMonth(year, month),
                            canGoPreviousMonth = true
                        )
                    }
                }
        }
    }

    fun previousMonth() {
        selectedMonth.update { (year, month) ->
            DateUtils.shiftMonth(year, month, -1)
        }
    }

    fun nextMonth() {
        selectedMonth.update { (year, month) ->
            if (hasNextMonth(year, month)) {
                DateUtils.shiftMonth(year, month, 1)
            } else {
                year to month
            }
        }
    }

    fun openRelapseDialog() {
        _uiState.update {
            it.copy(
                showRelapseDialog = true,
                relapseTrigger = "",
                relapseNote = "",
                errorMessage = null
            )
        }
    }

    fun closeRelapseDialog() {
        _uiState.update {
            it.copy(showRelapseDialog = false, relapseTrigger = "", relapseNote = "")
        }
    }

    fun updateRelapseTrigger(text: String) {
        _uiState.update { it.copy(relapseTrigger = text) }
    }

    fun updateRelapseNote(text: String) {
        _uiState.update { it.copy(relapseNote = text) }
    }

    fun saveRelapse() {
        val state = _uiState.value
        if (state.relapseTrigger.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = "Tuliskan pemicu kambuh terlebih dahulu.")
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.logRelapse(
                    triggerText = state.relapseTrigger,
                    note = state.relapseNote
                )
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        showRelapseDialog = false,
                        relapseTrigger = "",
                        relapseNote = "",
                        successMessage = "Evaluasi kambuh tersimpan. Streak bersih dimulai lagi besok."
                    )
                }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.localizedMessage ?: "Gagal menyimpan catatan kambuh."
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    // ---------------------------------------------------------------- helpers

    private fun hasNextMonth(year: Int, month: Int): Boolean =
        DateUtils.isAfterMonth(
            DateUtils.currentYear(),
            DateUtils.currentMonth(),
            year,
            month
        )

    private fun buildCalendar(
        summary: ProgressSummary,
        year: Int,
        month: Int
    ): List<CalendarDay> {
        val today = summary.todayKey.ifBlank { DateUtils.todayKey() }
        val days = mutableListOf<CalendarDay>()

        repeat(DateUtils.firstWeekdayIndex(year, month)) {
            days += CalendarDay(day = 0, status = DayStatus.EMPTY)
        }

        for (day in 1..DateUtils.daysInMonth(year, month)) {
            val dateKey = DateUtils.dayKey(year, month, day)
            val status = when {
                dateKey in summary.relapseDateKeys -> DayStatus.RELAPSE
                dateKey > today -> DayStatus.FUTURE
                dateKey in summary.activityDateKeys -> DayStatus.CLEAN
                else -> DayStatus.SKIP
            }
            days += CalendarDay(
                day = day,
                status = status,
                dateKey = dateKey,
                isToday = dateKey == today
            )
        }

        while (days.size % 7 != 0) {
            days += CalendarDay(day = 0, status = DayStatus.EMPTY)
        }

        return days
    }

    companion object {
        fun provideFactory(repository: ProgressRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
                        return ProgressViewModel(repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
