package com.example.mind_set.data.repository

import com.example.mind_set.data.local.dao.EmergencyLogDao
import com.example.mind_set.data.local.entity.EmergencyLogEntity
import com.example.mind_set.data.model.EmergencyIntervention
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
}

class DefaultEmergencyRepository(
    private val emergencyLogDao: EmergencyLogDao
    // Nanti tinggal tambahkan parameter di sini saat integrasi API AI / Supabase:
    // private val aiApiService: AiApiService? = null,
    // private val supabaseApiService: SupabaseApiService? = null
) : EmergencyRepository {

    override suspend fun processEmergencyAssessment(
        urgeLevel: Int,
        triggerCategory: String,
        additionalNote: String,
        excludeTitle: String?
    ): EmergencyIntervention = withContext(Dispatchers.IO) {

        // 1. Panggil CbtFallbackProvider untuk mendapatkan panduan CBT yang bervariasi
        //    dan tidak mengulang excludeTitle saat tombol "Coba Panduan Lain" ditekan.
        val intervention = CbtFallbackProvider.generate(
            urgeLevel = urgeLevel,
            triggerCategory = triggerCategory,
            excludeTitle = excludeTitle
        )

        // 2. Simpan hasil analisis ke Room Database lokal (isSynced = false untuk Supabase nanti)
        val combinedRecommendation =
            "${intervention.cbtTechniqueTitle}: ${intervention.recommendationSteps.joinToString(" | ")}"

        val entity = EmergencyLogEntity(
            urgeLevel = urgeLevel,
            triggerCategory = triggerCategory,
            additionalNote = additionalNote,
            recommendationGiven = combinedRecommendation,
            sourceType = intervention.sourceType,
            isSynced = false,
            createdAt = intervention.createdAt
        )

        val insertedId = emergencyLogDao.insertLog(entity).toInt()

        // 3. Kembalikan domain model dengan ID yang baru tersimpan
        intervention.copy(id = insertedId)
    }

    override fun getEmergencyHistory(): Flow<List<EmergencyIntervention>> {
        return emergencyLogDao.getAllLogs().map { entities ->
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