package com.example.mind_set.data.remote

import com.example.mind_set.BuildConfig
import com.example.mind_set.data.model.AiRecommendation
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.model.UserContextSummary
import com.example.mind_set.data.repository.JournalCbtFallbackProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class GeminiAiService(
    private val apiKeyProvider: () -> String = { BuildConfig.GEMINI_API_KEY },
    private val proxyUrlProvider: () -> String = { BuildConfig.AI_PROXY_URL }
) {

    companion object {
        private const val MODEL_NAME = "gemini-1.5-flash"
        private const val BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"
        private const val TIMEOUT_MS = 15_000
    }

    fun isApiConfigured(): Boolean =
        apiKeyProvider().isNotBlank() || proxyUrlProvider().isNotBlank()

    suspend fun generateDailyRecommendation(
        context: UserContextSummary
    ): Result<AiRecommendation> = withContext(Dispatchers.IO) {
        val prompt = buildDailyRecommendationPrompt(context)
        requestJsonFromGemini(prompt).mapCatching { rawJson ->
            val obj = JSONObject(rawJson)
            val title = obj.optString("title").takeIf { it.isNotBlank() }
                ?: error("Respons AI tidak memiliki judul rekomendasi")
            val description = obj.optString("description").takeIf { it.isNotBlank() }
                ?: error("Respons AI tidak memiliki deskripsi")
            val activityMethod = obj.optString("activity_method", "Latihan CBT Terarah")
            val durationLabel = obj.optString("duration_label", "5 Menit")
            val habitAvoidanceTip = obj.optString("habit_avoidance_tip", "")
            val warningMessage = obj.optString("warning_message", "").takeIf { it.isNotBlank() }
            val isHighRisk = obj.optBoolean("is_high_risk_pattern", false)

            val stepsJson = obj.optJSONArray("action_steps")
            val actionSteps = buildList {
                if (stepsJson != null) {
                    for (i in 0 until stepsJson.length()) {
                        val step = stepsJson.optString(i).trim()
                        if (step.isNotBlank()) add(step)
                    }
                }
            }.ifEmpty {
                listOf(
                    "Tarik napas perlahan selama 4 detik dan embuskan 6 detik.",
                    "Jauhkan perangkat digital selama 10 menit ke depan."
                )
            }

            AiRecommendation(
                title = title,
                description = description,
                activityMethod = activityMethod,
                durationLabel = durationLabel,
                actionSteps = actionSteps,
                habitAvoidanceTip = habitAvoidanceTip,
                warningMessage = warningMessage,
                isHighRiskPattern = isHighRisk,
                sourceType = "GEMINI_AI"
            )
        }
    }

    suspend fun analyzeJournalEntry(
        mood: String,
        triggerText: String,
        reframingText: String,
        tags: List<String>,
        recentHistory: List<String>
    ): Result<JournalCbtFallbackProvider.AnalysisResult> = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("Kamu adalah asisten pemulihan kesehatan mental berbasis Cognitive Behavioral Therapy (CBT) yang empatik, suportif, dan bebas stigma dalam bahasa Indonesia.")
            appendLine("Analisis entri jurnal harian berikut beserta riwayat singkat sebelumnya.")
            appendLine("Data Jurnal Baru:")
            appendLine("- Mood: $mood")
            appendLine("- Pemicu/Pikiran Otomatis: $triggerText")
            appendLine("- Reframing CBT Pengguna: ${reframingText.ifBlank { "(belum diisi)" }}")
            appendLine("- Tag Konteks: ${tags.joinToString(", ").ifBlank { "(tidak ada)" }}")
            if (recentHistory.isNotEmpty()) {
                appendLine("Riwayat Jurnal Sebelumnya:")
                recentHistory.take(3).forEach { appendLine("- $it") }
            }
            appendLine()
            appendLine("Kembalikan JSON valid dengan schema persis berikut:")
            appendLine("""{"ai_response": "string (maksimal 3 kalimat empatik & reflektif berbasis CBT)", "status": "Tantangan Teratasi" | "Refleksi Harian"}""")
        }

        requestJsonFromGemini(prompt).mapCatching { rawJson ->
            val obj = JSONObject(rawJson)
            val responseText = obj.optString("ai_response").takeIf { it.isNotBlank() }
                ?: error("Respons AI kosong")
            val status = obj.optString("status").takeIf {
                it == "Tantangan Teratasi" || it == "Refleksi Harian"
            } ?: "Refleksi Harian"

            JournalCbtFallbackProvider.AnalysisResult(
                aiResponse = "Respons CBT Gemini AI: $responseText",
                status = status
            )
        }
    }

    suspend fun analyzeEmergency(
        urgeLevel: Int,
        triggerCategory: String,
        additionalNote: String,
        excludeTitle: String? = null,
        retryCount: Int = 0
    ): Result<EmergencyIntervention> = withContext(Dispatchers.IO) {
        val urgencyStatus = when {
            urgeLevel >= 4 -> "HIGH"
            urgeLevel == 3 -> "MODERATE"
            else -> "LOW"
        }
        val prompt = buildString {
            appendLine("Kamu adalah asisten intervensi krisis CBT dalam bahasa Indonesia yang menenangkan, empatik, dan sangat praktis.")
            appendLine("Pengguna baru menekan Emergency Button (Pause & Ground) dengan kondisi:")
            appendLine("- Level Dorongan (1-5): $urgeLevel ($urgencyStatus)")
            appendLine("- Pemicu Utama: $triggerCategory")
            if (additionalNote.isNotBlank()) {
                appendLine("- Cerita / Catatan Pengguna: $additionalNote")
            }
            if (!excludeTitle.isNullOrBlank()) {
                appendLine("- Teknik sebelumnya yang BELUM mempan: \"$excludeTitle\" (percobaan ke-$retryCount). WAJIB berikan teknik CBT yang BERBEDA dan lebih intensif/praktis.")
            }
            appendLine()
            appendLine("Berikan 3 langkah konkret yang bisa langsung dilakukan dalam 2-5 menit ke depan dan relevan dengan cerita serta pemicu pengguna.")
            appendLine("Kembalikan JSON valid dengan schema persis berikut:")
            appendLine("""{"cbt_technique_title": "string judul teknik singkat", "recommendation_steps": ["langkah 1", "langkah 2", "langkah 3"]}""")
        }

        requestJsonFromGemini(prompt).mapCatching { rawJson ->
            val obj = JSONObject(rawJson)
            val title = obj.optString("cbt_technique_title").takeIf { it.isNotBlank() }
                ?: error("Judul teknik CBT kosong")
            val stepsArray = obj.optJSONArray("recommendation_steps")
            val steps = buildList {
                if (stepsArray != null) {
                    for (i in 0 until stepsArray.length()) {
                        val item = stepsArray.optString(i).trim()
                        if (item.isNotBlank()) add(item)
                    }
                }
            }.ifEmpty { error("Langkah rekomendasi kosong") }

            EmergencyIntervention(
                urgeLevel = urgeLevel,
                triggerCategory = triggerCategory,
                urgencyStatus = urgencyStatus,
                cbtTechniqueTitle = title,
                recommendationSteps = steps,
                sourceType = "GEMINI_AI"
            )
        }
    }

    suspend fun generateEmergencyClosing(
        urgeLevel: Int,
        triggerCategory: String,
        reflectionText: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("Kamu adalah pendamping pemulihan kesehatan mental berbasis Cognitive Behavioral Therapy (CBT) dalam bahasa Indonesia.")
            appendLine("Pengguna baru saja melewati momen dorongan kuat (Emergency Button) dan menuliskan refleksi pribadinya:")
            appendLine("- Level Dorongan: $urgeLevel/5")
            appendLine("- Pemicu: $triggerCategory")
            appendLine("- Refleksi Pengguna: $reflectionText")
            appendLine()
            appendLine("Berikan pesan penutup yang hangat, validatif, bebas menghakimi, dan menguatkan langkah berikutnya (maksimal 3 kalimat).")
            appendLine("Kembalikan JSON valid dengan schema persis berikut:")
            appendLine("""{"closing_message": "string pesan penutup empatik"}""")
        }

        requestJsonFromGemini(prompt).mapCatching { rawJson ->
            val obj = JSONObject(rawJson)
            obj.optString("closing_message").takeIf { it.isNotBlank() }
                ?: error("Pesan penutup AI kosong")
        }
    }

    private fun buildDailyRecommendationPrompt(context: UserContextSummary): String {
        return buildString {
            appendLine("Kamu adalah sistem rekomendasi AI pendamping pemulihan kecanduan digital berbasis Cognitive Behavioral Therapy (CBT) pada aplikasi Mind-Set.")
            appendLine("Tugasmu menganalisis pola jurnal harian, data progress/streak, serta riwayat tombol darurat pengguna, lalu menyusun rekomendasi kegiatan positif, cara menghindari kebiasaan buruk, dan peringatan intervensi jika terdeteksi pola tidak sehat.")
            appendLine()
            appendLine("Data Konteks Pengguna Saat Ini:")
            appendLine("- Mood saat ini di Beranda: ${context.currentMood.ifBlank { "Belum dipilih" }}")
            appendLine("- Clean Streak: ${context.currentStreakDays} hari")
            appendLine("- Rasio Keberhasilan: ${"%.1f".format(context.successRatioPercent)}%")
            appendLine("- Total Jurnal Tercatat: ${context.totalJournals}")
            appendLine("- Total Sesi Emergency: ${context.totalEmergencySessions}")
            appendLine("- Pemicu Dominan: ${context.dominantTriggers.joinToString(", ").ifBlank { "Belum terdeteksi" }}")
            appendLine("- Ringkasan Jurnal Terbaru: ${context.recentJournalSummary}")
            appendLine("- Ringkasan Emergency Terbaru: ${context.recentEmergencySummary}")
            appendLine()
            appendLine("Berikan respons dalam format JSON valid berbahasa Indonesia dengan schema persis berikut:")
            appendLine(
                """
                {
                  "title": "Judul rekomendasi singkat & menarik (maks 8 kata)",
                  "description": "Penjelasan personal mengapa rekomendasi ini cocok berdasarkan pola pengguna (maks 2 kalimat)",
                  "activity_method": "Nama metode CBT / aktivitas (misal: Metode 5-4-3-2-1)",
                  "duration_label": "Durasi singkat (misal: 5 Menit)",
                  "action_steps": ["Langkah 1 konkret", "Langkah 2 konkret", "Langkah 3 konkret"],
                  "habit_avoidance_tip": "1 kalimat tips praktis menghindari pemicu kebiasaan buruk",
                  "is_high_risk_pattern": true atau false,
                  "warning_message": "Pesan peringatan/intervensi lembut jika is_high_risk_pattern true, atau string kosong jika aman"
                }
                """.trimIndent()
            )
        }
    }

    private fun requestJsonFromGemini(prompt: String): Result<String> {
        val apiKey = apiKeyProvider().trim()
        val proxyUrl = proxyUrlProvider().trim()

        if (apiKey.isBlank() && proxyUrl.isBlank()) {
            return Result.failure(
                IllegalStateException("API Key Gemini maupun AI Proxy URL belum dikonfigurasi.")
            )
        }

        val useDirectApi = apiKey.isNotBlank()

        return runCatching {
            val url = if (useDirectApi) URL("$BASE_URL?key=$apiKey") else URL(proxyUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            }

            try {
                val requestBody = if (useDirectApi) {
                    JSONObject().apply {
                        put(
                            "contents",
                            JSONArray().put(
                                JSONObject().put(
                                    "parts",
                                    JSONArray().put(JSONObject().put("text", prompt))
                                )
                            )
                        )
                        put(
                            "generationConfig",
                            JSONObject().apply {
                                put("temperature", 0.5)
                                put("responseMimeType", "application/json")
                            }
                        )
                    }
                } else {
                    JSONObject().apply {
                        put("prompt", prompt)
                    }
                }

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(requestBody.toString())
                    writer.flush()
                }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val responseBody = stream?.let {
                    BufferedReader(InputStreamReader(it, Charsets.UTF_8)).use { reader ->
                        reader.readText()
                    }
                }.orEmpty()

                if (responseCode !in 200..299) {
                    error("HTTP $responseCode dari API AI: ${responseBody.take(180)}")
                }

                val root = JSONObject(responseBody)
                val candidates = root.optJSONArray("candidates")
                    ?: error("Format respons API AI tidak valid (candidates kosong)")
                val firstCandidate = candidates.optJSONObject(0)
                    ?: error("Candidate pertama kosong")
                val content = firstCandidate.optJSONObject("content")
                    ?: error("Content API AI kosong")
                val parts = content.optJSONArray("parts")
                    ?: error("Parts API AI kosong")
                val rawText = parts.optJSONObject(0)?.optString("text").orEmpty().trim()
                if (rawText.isBlank()) error("Teks respons AI kosong")

                rawText
                    .removePrefix("```json")
                    .removePrefix("```")
                    .removeSuffix("```")
                    .trim()
            } finally {
                connection.disconnect()
            }
        }
    }
}
