package com.example.mind_set.data.repository

import android.content.Context
import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.entity.EmergencyLogEntity
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.remote.GeminiAiService
import com.example.mind_set.data.remote.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface EmergencyRepository {
    suspend fun processEmergencyAssessment(
        urgeLevel: Int,
        triggerCategory: String,
        additionalNote: String = "",
        excludeTitle: String? = null
    ): EmergencyIntervention

    fun getEmergencyHistory(): Flow<List<EmergencyIntervention>>

    /**
     * Menyimpan refleksi detail pengguna ke Room DB dan menandai sesi sebagai selesai.
     * Mengembalikan ID log yang baru disimpan/diperbarui.
     */
    suspend fun submitReflectionAndClose(
        urgeLevel: Int,
        triggerCategory: String,
        reflectionNote: String,
        existingLogId: Int? = null
    ): Int
}

class DefaultEmergencyRepository(
    private val emergencyLogDao: EmergencyLogDao,
    private val context: Context? = null,
    private val geminiAiService: GeminiAiService? = null,
    private val username: String = "default_user"
) : EmergencyRepository {

    override suspend fun processEmergencyAssessment(
        urgeLevel: Int,
        triggerCategory: String,
        additionalNote: String,
        excludeTitle: String?
    ): EmergencyIntervention = withContext(Dispatchers.IO) {

        val canUseAi = excludeTitle == null &&
            context != null &&
            geminiAiService != null &&
            NetworkUtils.isInternetAvailable(context) &&
            geminiAiService.isApiConfigured()

        val intervention = if (canUseAi) {
            geminiAiService!!.analyzeEmergency(
                urgeLevel = urgeLevel,
                triggerCategory = triggerCategory,
                additionalNote = additionalNote
            ).getOrElse {
                CbtFallbackProvider.generate(
                    urgeLevel = urgeLevel,
                    triggerCategory = triggerCategory,
                    excludeTitle = excludeTitle
                )
            }
        } else {
            CbtFallbackProvider.generate(
                urgeLevel = urgeLevel,
                triggerCategory = triggerCategory,
                excludeTitle = excludeTitle
            )
        }

        val combinedRecommendation =
            "${intervention.cbtTechniqueTitle}: ${intervention.recommendationSteps.joinToString(" | ")}"

        val entity = EmergencyLogEntity(
            username = username,
            urgeLevel = urgeLevel,
            triggerCategory = triggerCategory,
            additionalNote = additionalNote,
            recommendationGiven = combinedRecommendation,
            sourceType = intervention.sourceType,
            isSynced = false,
            isSessionCompleted = false,
            createdAt = intervention.createdAt
        )

        val insertedId = emergencyLogDao.insertLog(entity).toInt()

        intervention.copy(id = insertedId)
    }

    override suspend fun submitReflectionAndClose(
        urgeLevel: Int,
        triggerCategory: String,
        reflectionNote: String,
        existingLogId: Int?
    ): Int = withContext(Dispatchers.IO) {

        if (existingLogId != null) {
            emergencyLogDao.finalizeWithReflection(
                logId = existingLogId,
                note = reflectionNote
            )
            existingLogId
        } else {
            val entity = EmergencyLogEntity(
                username = username,
                urgeLevel = urgeLevel,
                triggerCategory = triggerCategory,
                additionalNote = reflectionNote,
                recommendationGiven = "Sesi Refleksi Krisis – Disimpan Offline",
                sourceType = "REFLECTION_OFFLINE",
                isSynced = false,
                isSessionCompleted = true
            )
            emergencyLogDao.insertLog(entity).toInt()
        }
    }

    override fun getEmergencyHistory(): Flow<List<EmergencyIntervention>> {
        return emergencyLogDao.getAllLogs(username).map { entities ->
            entities.map { entity ->
                EmergencyIntervention(
                    id = entity.id,
                    urgeLevel = entity.urgeLevel,
                    triggerCategory = entity.triggerCategory,
                    urgencyStatus = when {
                        entity.urgeLevel >= 4 -> "HIGH"
                        entity.urgeLevel == 3 -> "MODERATE"
                        else -> "LOW"
                    },
                    cbtTechniqueTitle = entity.recommendationGiven.substringBefore(":"),
                    recommendationSteps = entity.recommendationGiven
                        .substringAfter(": ", entity.recommendationGiven)
                        .split(" | "),
                    sourceType = entity.sourceType,
                    createdAt = entity.createdAt
                )
            }
        }
    }
}
