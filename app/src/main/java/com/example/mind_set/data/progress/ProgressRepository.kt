package com.example.mind_set.data.progress

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Repository untuk Progress & Streak Tracking.
 * Penyimpanan: SQLite lokal (via ProgressDbHelper).
 * State diekspos sebagai StateFlow agar UI Compose bisa mengamati perubahan.
 */
class ProgressRepository private constructor(context: Context) {

    private val db = ProgressDbHelper(context.applicationContext)
    private val _state = MutableStateFlow(ProgressSnapshot())
    val state: StateFlow<ProgressSnapshot> = _state.asStateFlow()

    private var initialized = false

    suspend fun initialize() {
        if (initialized) return
        withContext(Dispatchers.IO) {
            seedIfEmpty()
            refresh()
        }
        initialized = true
    }

    suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val checkIns = db.getAllCheckIns()
            val total = checkIns.size
            val success = checkIns.count { it.status == CheckInStatus.SUCCESS }
            val relapse = checkIns.count { it.status == CheckInStatus.RELAPSE }
            val ratio = ProgressCalculator.successRatio(success, total)
            val snap = ProgressSnapshot(
                currentStreak = ProgressCalculator.currentStreak(checkIns),
                longestStreak = ProgressCalculator.longestStreak(checkIns),
                totalDays = total,
                successDays = success,
                relapseDays = relapse,
                successRatio = ratio,
                weeklyConsistency = ProgressCalculator.weeklyConsistency(checkIns, ProgressCalculator.todayString()),
                todayStatus = checkIns.find { it.date == ProgressCalculator.todayString() }?.status,
                checkIns = checkIns.sortedByDescending { it.date },
                lastCheckInDate = checkIns.maxByOrNull { it.date }?.date,
                updatedAt = System.currentTimeMillis()
            )
            _state.value = snap
        }
    }

    suspend fun recordOutcome(
        date: String,
        status: CheckInStatus,
        intensity: Int? = null,
        triggers: String? = null,
        note: String? = null
    ) {
        withContext(Dispatchers.IO) {
            val existing = db.getCheckIn(date)
            db.upsertCheckIn(
                DailyCheckIn(
                    date = date,
                    status = status,
                    intensity = intensity,
                    triggers = triggers,
                    note = note,
                    createdAt = existing?.createdAt ?: System.currentTimeMillis()
                )
            )
            refresh()
        }
    }

    suspend fun deleteOutcome(date: String) {
        withContext(Dispatchers.IO) {
            db.deleteCheckIn(date)
            refresh()
        }
    }

    suspend fun resetAll() {
        withContext(Dispatchers.IO) {
            db.clearAll()
            refresh()
        }
    }

    private fun seedIfEmpty() {
        if (db.getAllCheckIns().isNotEmpty()) return
        val today = ProgressCalculator.todayString()
        // Seed: 1 kambuh 20 hari lalu + 14 hari bersih berturut-turut sampai kemarin.
        db.upsertCheckIn(DailyCheckIn(addDays(today, -20), CheckInStatus.RELAPSE, 4, "Kelelahan", "Kambuh saat lelah"))
        for (i in 14 downTo 1) {
            db.upsertCheckIn(DailyCheckIn(addDays(today, -i), CheckInStatus.SUCCESS))
        }
    }

    private fun addDays(dateStr: String, days: Int): String =
        ProgressCalculator.addDays(dateStr, days)

    companion object {
        @Volatile
        private var instance: ProgressRepository? = null

        fun get(context: Context): ProgressRepository {
            return instance ?: synchronized(this) {
                instance ?: ProgressRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}