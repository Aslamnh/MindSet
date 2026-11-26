package com.example.mind_set.data.repository

import android.content.Context
import com.example.mind_set.data.local.dao.AiRecommendationDao
import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.dao.JournalDao
import com.example.mind_set.data.local.entity.AiRecommendationEntity
import com.example.mind_set.data.model.AiRecommendation
import com.example.mind_set.data.model.UserContextSummary
import com.example.mind_set.data.remote.GeminiAiService
import com.example.mind_set.data.remote.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getOrElse

data class RecoveryStats(
    val streakDays: Int = 1,
    val targetDays: Int = 21,
    val successRatioPercent: Double = 100.0,
    val totalJournals: Int = 0,
    val totalEmergencySessions: Int = 0,
    val hasJournalToday: Boolean = false
)

data class RecommendationGenerationResult(
    val recommendation: AiRecommendation,
    val apiErrorNotice: String? = null
)

interface AiRecommendationRepository {
    fun getLatestRecommendation(): Flow<AiRecommendation?>
    fun getRecoveryStats(): Flow<RecoveryStats>
    suspend fun generateAndSaveRecommendation(currentMood: String): RecommendationGenerationResult
    suspend fun toggleBookmark(id: Int, isBookmarked: Boolean)
}

class DefaultAiRecommendationRepository(
    private val context: Context,
    private val journalDao: JournalDao,
    private val emergencyLogDao: EmergencyLogDao,
    private val aiRecommendationDao: AiRecommendationDao,
    private val geminiAiService: GeminiAiService,
    private val username: String = "default_user"
) : AiRecommendationRepository {

    override fun getLatestRecommendation(): Flow<AiRecommendation?> {
        return aiRecommendationDao.getLatestRecommendation().map { entity ->
            entity?.toDomain()
        }
    }

    override fun getRecoveryStats(): Flow<RecoveryStats> {
        return combine(
            journalDao.getAllJournals(username),
            emergencyLogDao.getAllLogs(username)
        ) { journals, logs ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayKey = dateFormat.format(Date())

            val journalDays = journals.map { dateFormat.format(Date(it.createdAt)) }.toSet()
            val hasJournalToday = journalDays.contains(todayKey)

            val streakDays = maxOf(1, journalDays.size)
            val targetDays = when {
                streakDays < 3 -> 3
                streakDays < 7 -> 7
                streakDays < 21 -> 21
                else -> 90
            }

            val completedEmergencies = logs.count { it.isSessionCompleted }
            val totalActions = journals.size + logs.size
            val successfulActions = journals.count {
                it.status == "Tantangan Teratasi" || it.reframingText.isNotBlank()
            } + completedEmergencies

            val successRatio = if (totalActions == 0) {
                100.0
            } else {
                ((successfulActions.toDouble() / totalActions.toDouble()) * 100.0)
                    .coerceIn(0.0, 100.0)
            }

            RecoveryStats(
                streakDays = streakDays,
                targetDays = targetDays,
                successRatioPercent = successRatio,
                totalJournals = journals.size,
                totalEmergencySessions = logs.size,
                hasJournalToday = hasJournalToday
            )
        }
    }

    override suspend fun generateAndSaveRecommendation(
        currentMood: String
    ): RecommendationGenerationResult = withContext(Dispatchers.IO) {
        val recentJournals = journalDao.getRecentJournals(username, 5)
        val recentEmergencies = emergencyLogDao.getRecentLogs(username, 5)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val uniqueDays = recentJournals.map { dateFormat.format(Date(it.createdAt)) }.toSet().size
        val streakDays = maxOf(1, uniqueDays)

        val totalActions = recentJournals.size + recentEmergencies.size
        val successfulActions = recentJournals.count {
            it.status == "Tantangan Teratasi" || it.reframingText.isNotBlank()
        } + recentEmergencies.count { it.isSessionCompleted }
        val successRatio = if (totalActions == 0) {
            100.0
        } else {
            (successfulActions.toDouble() / totalActions.toDouble()) * 100.0
        }

        val triggerTags = recentJournals
            .flatMap { it.tags.split(",") }
            .map { it.trim() }
            .filter { it.isNotBlank() }
        val emergencyTriggers = recentEmergencies
            .map { it.triggerCategory.substringAfter(" ").trim() }
            .filter { it.isNotBlank() }
        val dominantTriggers = (triggerTags + emergencyTriggers)
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        val journalSummary = if (recentJournals.isEmpty()) {
            "Belum ada catatan jurnal."
        } else {
            recentJournals.take(3).joinToString(" | ") {
                "[Mood: ${it.mood}, Pemicu: ${it.triggerText.take(60)}]"
            }
        }

        val emergencySummary = if (recentEmergencies.isEmpty()) {
            "Belum ada riwayat darurat."
        } else {
            recentEmergencies.take(3).joinToString(" | ") {
                "[Level ${it.urgeLevel}/5, Pemicu: ${it.triggerCategory}]"
            }
        }

        val contextSummary = UserContextSummary(
            currentMood = currentMood,
            currentStreakDays = streakDays,
            successRatioPercent = successRatio,
            totalJournals = recentJournals.size,
            totalEmergencySessions = recentEmergencies.size,
            dominantTriggers = dominantTriggers,
            recentJournalSummary = journalSummary,
            recentEmergencySummary = emergencySummary
        )

        val hasNetworkAndKey = NetworkUtils.isInternetAvailable(context) && geminiAiService.isApiConfigured()
        var apiErrorNotice: String? = null

        val generated = if (hasNetworkAndKey) {
            geminiAiService.generateDailyRecommendation(contextSummary)
                .getOrElse { error ->
                    apiErrorNotice = "API AI tidak merespons (${error.localizedMessage ?: "error"}), menggunakan analisis lokal."
                    AiRecommendationFallbackProvider.generate(contextSummary)
                }
        } else {
            if (!geminiAiService.isApiConfigured()) {
                apiErrorNotice = "Mode lokal aktif (API Key Gemini belum diatur di local.properties)."
            } else {
                apiErrorNotice = "Koneksi offline, menggunakan rekomendasi rule-based lokal."
            }
            AiRecommendationFallbackProvider.generate(contextSummary)
        }

        val insertedId = aiRecommendationDao.insertRecommendation(generated.toEntity()).toInt()
        RecommendationGenerationResult(
            recommendation = generated.copy(id = insertedId),
            apiErrorNotice = apiErrorNotice
        )
    }

    override suspend fun toggleBookmark(id: Int, isBookmarked: Boolean) = withContext(Dispatchers.IO) {
        aiRecommendationDao.updateBookmark(id, isBookmarked)
    }

    private fun AiRecommendationEntity.toDomain(): AiRecommendation {
        return AiRecommendation(
            id = id,
            title = title,
            description = description,
            activityMethod = activityMethod,
            durationLabel = durationLabel,
            actionSteps = actionSteps.split(" | ").filter { it.isNotBlank() },
            habitAvoidanceTip = habitAvoidanceTip,
            warningMessage = warningMessage,
            isHighRiskPattern = isHighRiskPattern,
            sourceType = sourceType,
            isBookmarked = isBookmarked,
            createdAt = createdAt
        )
    }

    private fun AiRecommendation.toEntity(): AiRecommendationEntity {
        return AiRecommendationEntity(
            title = title,
            description = description,
            activityMethod = activityMethod,
            durationLabel = durationLabel,
            actionSteps = actionSteps.joinToString(" | "),
            habitAvoidanceTip = habitAvoidanceTip,
            warningMessage = warningMessage,
            isHighRiskPattern = isHighRiskPattern,
            sourceType = sourceType,
            isBookmarked = isBookmarked,
            createdAt = createdAt
        )
    }
}
