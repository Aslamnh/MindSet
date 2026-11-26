package com.example.mind_set.data.repository

import com.example.mind_set.data.local.dao.DailyCheckInDao
import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.dao.JournalDao
import com.example.mind_set.data.local.dao.RelapseLogDao
import com.example.mind_set.data.local.entity.DailyCheckInEntity
import com.example.mind_set.data.local.entity.RelapseLogEntity
import com.example.mind_set.data.model.CbtPhaseInfo
import com.example.mind_set.data.model.ProgressSummary
import com.example.mind_set.data.model.RelapseLogItem
import com.example.mind_set.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

interface ProgressRepository {
    fun getProgress(): Flow<ProgressSummary>
    suspend fun recordDailyCheckIn()
    suspend fun logRelapse(triggerText: String, note: String)
    suspend fun deleteRelapse(id: Int)
}

class DefaultProgressRepository(
    private val journalDao: JournalDao,
    private val emergencyLogDao: EmergencyLogDao,
    private val checkInDao: DailyCheckInDao,
    private val relapseLogDao: RelapseLogDao,
    private val username: String = "default_user"
) : ProgressRepository {

    override fun getProgress(): Flow<ProgressSummary> = combine(
        journalDao.getAllJournals(username),
        emergencyLogDao.getAllLogs(username),
        checkInDao.getAllCheckIns(username),
        relapseLogDao.getAllRelapses(username)
    ) { journals, emergencies, checkIns, relapses ->
        buildSummary(
            journalKeys = journals.map { DateUtils.keyOf(it.createdAt) }.toSet(),
            emergencyKeys = emergencies.map { DateUtils.keyOf(it.createdAt) }.toSet(),
            checkInKeys = checkIns.map { it.dateKey }.toSet(),
            relapses = relapses.map {
                RelapseLogItem(
                    id = it.id,
                    dateKey = it.dateKey,
                    triggerText = it.triggerText,
                    note = it.note,
                    createdAt = it.createdAt
                )
            }
        )
    }

    override suspend fun recordDailyCheckIn() = withContext(Dispatchers.IO) {
        val today = DateUtils.todayKey()
        if (checkInDao.countForDate(today, username) == 0) {
            checkInDao.insertCheckIn(DailyCheckInEntity(dateKey = today, username = username))
        }
    }

    override suspend fun logRelapse(triggerText: String, note: String) {
        withContext(Dispatchers.IO) {
            relapseLogDao.insertRelapse(
                RelapseLogEntity(
                    username = username,
                    dateKey = DateUtils.todayKey(),
                    triggerText = triggerText.trim(),
                    note = note.trim()
                )
            )
        }
    }

    override suspend fun deleteRelapse(id: Int) = withContext(Dispatchers.IO) {
        relapseLogDao.deleteRelapse(id)
    }

    private fun buildSummary(
        journalKeys: Set<String>,
        emergencyKeys: Set<String>,
        checkInKeys: Set<String>,
        relapses: List<RelapseLogItem>
    ): ProgressSummary {
        val today = DateUtils.todayKey()
        val activityKeys = journalKeys + emergencyKeys + checkInKeys
        val relapseKeys = relapses.map { it.dateKey }.toSet()

        val trackingStart = (activityKeys + relapseKeys).minOrNull() ?: today
        val totalTrackedDays =
            (DateUtils.daysBetween(trackingStart, today) + 1).coerceAtLeast(1)

        val pastRelapseKeys = relapseKeys.filter { it <= today }.sorted()
        val lastRelapseKey = pastRelapseKeys.lastOrNull()

        val totalCleanDays = (totalTrackedDays - pastRelapseKeys.count { it >= trackingStart })
            .coerceAtLeast(0)

        val currentCleanStreak = when {
            lastRelapseKey == null -> DateUtils.daysBetween(trackingStart, today) + 1
            lastRelapseKey == today -> 0
            else -> DateUtils.daysBetween(lastRelapseKey, today)
        }.coerceAtLeast(0)

        val longestCleanStreak = calculateLongestStreak(
            trackingStart = trackingStart,
            today = today,
            relapseKeys = pastRelapseKeys,
            currentStreak = currentCleanStreak
        )

        val currentJournalStreak = calculateJournalStreak(journalKeys, today)

        val monthStartKey = DateUtils.dayKey(
            DateUtils.currentYear(),
            DateUtils.currentMonth(),
            1
        )
        val monthEndKey = DateUtils.dayKey(
            DateUtils.currentYear(),
            DateUtils.currentMonth(),
            DateUtils.daysInMonth(DateUtils.currentYear(), DateUtils.currentMonth())
        )
        val activeThisMonth = activityKeys.count { it in monthStartKey..monthEndKey && it <= today }
        val elapsedThisMonth = (DateUtils.daysBetween(monthStartKey, today) + 1).coerceAtLeast(1)

        val successRate = totalCleanDays.toFloat() / totalTrackedDays
        val consistencyRate = activeThisMonth.toFloat() / elapsedThisMonth

        return ProgressSummary(
            todayKey = today,
            currentCleanStreak = currentCleanStreak,
            longestCleanStreak = longestCleanStreak,
            currentJournalStreak = currentJournalStreak,
            totalCleanDays = totalCleanDays,
            totalTrackedDays = totalTrackedDays,
            successRate = successRate.coerceIn(0f, 1f),
            consistencyRate = consistencyRate.coerceIn(0f, 1f),
            activeDaysThisMonth = activeThisMonth,
            elapsedDaysThisMonth = elapsedThisMonth,
            trackingStartKey = trackingStart,
            lastRelapse = relapses.maxByOrNull { it.createdAt },
            relapseDateKeys = relapseKeys,
            activityDateKeys = activityKeys,
            phases = buildPhases(currentCleanStreak),
            affirmation = buildAffirmation(currentCleanStreak, lastRelapseKey, today)
        )
    }

    private fun calculateLongestStreak(
        trackingStart: String,
        today: String,
        relapseKeys: List<String>,
        currentStreak: Int
    ): Int {
        if (relapseKeys.isEmpty()) return currentStreak

        val runs = mutableListOf<Int>()
        runs += DateUtils.daysBetween(trackingStart, relapseKeys.first())
        relapseKeys.zipWithNext { previous, next ->
            runs += DateUtils.daysBetween(previous, next)
        }
        runs += DateUtils.daysBetween(relapseKeys.last(), today)

        return (runs.maxOrNull() ?: 0).coerceAtLeast(currentStreak)
    }

    private fun calculateJournalStreak(journalKeys: Set<String>, today: String): Int {
        val yesterday = DateUtils.shiftDay(today, -1)
        val anchor = when {
            today in journalKeys -> today
            yesterday in journalKeys -> yesterday
            else -> return 0
        }

        var count = 0
        var cursor = anchor
        while (cursor in journalKeys) {
            count++
            cursor = DateUtils.shiftDay(cursor, -1)
        }
        return count
    }

    private fun buildPhases(streak: Int): List<CbtPhaseInfo> {
        val specs = listOf(
            PhaseSpec("Fase 1: 3 Hari Detoks", "Pemutusan impuls otomatis", 0, 3),
            PhaseSpec("Fase 2: 7 Hari Kesadaran Diri", "Mengenali pola pemicu kognitif", 3, 7),
            PhaseSpec("Fase 3: 21 Hari Pola Baru", "Membentuk kebiasaan sehat baru", 7, 21),
            PhaseSpec(
                "Fase 4: 90 Hari Ketahanan Otomatis",
                "Gaya hidup stabil & autonomi penuh",
                21,
                90
            )
        )

        return specs.map { spec ->
            val completed = streak >= spec.end
            val active = streak in spec.start until spec.end
            val locked = streak < spec.start
            val progress = when {
                completed -> 1f
                active -> ((streak - spec.start).toFloat() / (spec.end - spec.start))
                    .coerceIn(0f, 1f)
                else -> 0f
            }

            CbtPhaseInfo(
                title = spec.title,
                subtitle = when {
                    completed -> "Selesai — fondasi fase ini sudah terbentuk"
                    active -> "Fase Berlangsung ($streak/${spec.end} • ${(progress * 100).toInt()}%)"
                    else -> spec.description
                },
                progress = progress,
                completed = completed,
                locked = locked
            )
        }
    }

    private fun buildAffirmation(
        streak: Int,
        lastRelapseKey: String?,
        today: String
    ): String = when {
        lastRelapseKey == today ->
            "Kambuh hari ini bukan akhir perjalanan. Rasio keberhasilanmu tetap tercatat — " +
                "evaluasi pemicunya, lalu mulai lagi esok hari dengan welas asih pada diri sendiri."

        streak >= 90 ->
            "90 hari ketahanan! Jalur neuroplastisitas baru sudah terbentuk kuat. " +
                "Pertahankan ritme dan bagikan strategimu pada dirimu yang dulu."

        streak >= 21 ->
            "21 hari pola baru! Kamu membuktikan bahwa perubahan bukan soal sempurna, " +
                "tapi konsisten. Teruskan satu hari demi satu hari."

        streak >= 7 ->
            "7 hari kesadaran diri tercapai. Kamu makin mengenali pemicu dan cara meresponsnya. " +
                "Ini kemajuan nyata."

        streak >= 3 ->
            "3 hari detoks berhasil! Fondasi awal sudah terbentuk. " +
                "Lanjutkan ke fase kesadaran diri."

        streak >= 1 ->
            "Hari ini kamu memilih untuk bertahan. Itu kemenangan kecil yang layak dirayakan."

        else ->
            "Setiap perjalanan dimulai dari satu langkah. Catat jurnal hari ini dan mulai bangun streak-mu."
    }

    private data class PhaseSpec(
        val title: String,
        val description: String,
        val start: Int,
        val end: Int
    )
}
