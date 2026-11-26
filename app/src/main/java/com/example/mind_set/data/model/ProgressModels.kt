package com.example.mind_set.data.model

/** Status satu hari pada kalender konsistensi. */
enum class DayStatus {
    CLEAN,   // ada aktivitas (jurnal / pause / log-in)
    RELAPSE, // pengguna mencatat kambuh
    SKIP,    // tidak ada aktivitas
    FUTURE,  // tanggal belum terjadi
    EMPTY    // sel kosong sebelum tanggal 1 / padding
}

data class CalendarDay(
    val day: Int,
    val status: DayStatus,
    val dateKey: String? = null,
    val isToday: Boolean = false
)

data class RelapseLogItem(
    val id: Int,
    val dateKey: String,
    val triggerText: String,
    val note: String,
    val createdAt: Long
)

data class CbtPhaseInfo(
    val title: String,
    val subtitle: String,
    val progress: Float,
    val completed: Boolean,
    val locked: Boolean
)

/**
 * Ringkasan seluruh metrik Progress & Streak yang sudah dihitung dari database.
 * Semua angka di layar Progress berasal dari objek ini (tidak ada yang hardcoded).
 */
data class ProgressSummary(
    val todayKey: String = "",
    /** Hari berturut-turut tanpa kambuh. */
    val currentCleanStreak: Int = 0,
    val longestCleanStreak: Int = 0,
    /** Hari berturut-turut mengisi jurnal (dengan toleransi hari ini belum diisi). */
    val currentJournalStreak: Int = 0,
    val totalCleanDays: Int = 0,
    val totalTrackedDays: Int = 0,
    val successRate: Float = 0f,
    val consistencyRate: Float = 0f,
    val activeDaysThisMonth: Int = 0,
    val elapsedDaysThisMonth: Int = 0,
    val trackingStartKey: String? = null,
    val lastRelapse: RelapseLogItem? = null,
    val relapseDateKeys: Set<String> = emptySet(),
    val activityDateKeys: Set<String> = emptySet(),
    val phases: List<CbtPhaseInfo> = emptyList(),
    val affirmation: String = ""
)
