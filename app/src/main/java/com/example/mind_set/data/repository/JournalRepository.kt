package com.example.mind_set.data.repository

import android.content.Context
import com.example.mind_set.data.local.dao.JournalDao
import com.example.mind_set.data.local.entity.JournalEntity
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.data.remote.GeminiAiService
import com.example.mind_set.data.remote.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

interface JournalRepository {
    suspend fun saveJournal(
        mood: String,
        triggerText: String,
        reframingText: String,
        tags: List<String>
    ): Int

    fun getAllJournals(): Flow<List<JournalItem>>

    suspend fun deleteJournal(id: Int)
}

class DefaultJournalRepository(
    private val journalDao: JournalDao,
    private val context: Context? = null,
    private val geminiAiService: GeminiAiService? = null
) : JournalRepository {

    override suspend fun saveJournal(
        mood: String,
        triggerText: String,
        reframingText: String,
        tags: List<String>
    ): Int = withContext(Dispatchers.IO) {
        val recentHistory = journalDao.getRecentJournals(3).map {
            "${it.mood}: ${it.triggerText.take(80)}"
        }

        val canUseAi = context != null &&
            geminiAiService != null &&
            NetworkUtils.isInternetAvailable(context) &&
            geminiAiService.isApiConfigured()

        val analysis = if (canUseAi) {
            geminiAiService!!.analyzeJournalEntry(
                mood = mood,
                triggerText = triggerText,
                reframingText = reframingText,
                tags = tags,
                recentHistory = recentHistory
            ).getOrElse {
                JournalCbtFallbackProvider.analyze(mood, triggerText, reframingText, tags)
            }
        } else {
            JournalCbtFallbackProvider.analyze(mood, triggerText, reframingText, tags)
        }

        val entity = JournalEntity(
            mood = mood,
            triggerText = triggerText,
            reframingText = reframingText,
            tags = tags.joinToString(","),
            aiResponse = analysis.aiResponse,
            status = analysis.status,
            createdAt = System.currentTimeMillis()
        )

        journalDao.insertJournal(entity).toInt()
    }

    override fun getAllJournals(): Flow<List<JournalItem>> {
        return journalDao.getAllJournals().map { entities ->
            entities.map { entity ->
                JournalItem(
                    id = entity.id,
                    date = entity.date,
                    mood = entity.mood,
                    triggerText = entity.triggerText,
                    reframingText = entity.reframingText,
                    tags = if (entity.tags.isNotBlank()) entity.tags.split(",") else emptyList(),
                    aiResponse = entity.aiResponse,
                    status = entity.status,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override suspend fun deleteJournal(id: Int) = withContext(Dispatchers.IO) {
        journalDao.deleteJournal(id)
    }
}
