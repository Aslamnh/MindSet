package com.example.mind_set.data.progress

/**
 * Status pemulihan untuk satu hari.
 * - SUCCESS : berhasil menahan godaan (hari bersih).
 * - RELAPSE : mengalami kekambuhan.
 * - SKIP    : hari terlewat tanpa catatan.
 */
enum class CheckInStatus { SUCCESS, RELAPSE, SKIP }

/**
 * Satu catatan harian (check-in) yang dipakai untuk menghitung streak & progres.
 */
data class DailyCheckIn(
    val date: String,                       // format "yyyy-MM-dd"
    val status: CheckInStatus,
    val intensity: Int? = null,            // 1..5 (skala dorongan)
    val triggers: String? = null,          // pemicu yang dipilih, dipisah koma
    val note: String? = null,              // catatan singkat
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Ringkasan progres yang dikonsumsi UI.
 */
data class ProgressSnapshot(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalDays: Int = 0,
    val successDays: Int = 0,
    val relapseDays: Int = 0,
    val successRatio: Float = 0f,
    val weeklyConsistency: Float = 0f,
    val todayStatus: CheckInStatus? = null,
    val checkIns: List<DailyCheckIn> = emptyList(),
    val lastCheckInDate: String? = null,
    val updatedAt: Long = 0L
)

/**
 * Status sel untuk kalender heatmap (dipakai ProgressScreen).
 */
enum class DayStatus { CLEAN, RELAPSE, SKIP, FUTURE, EMPTY }

data class CalendarDay(val day: Int, val status: DayStatus)