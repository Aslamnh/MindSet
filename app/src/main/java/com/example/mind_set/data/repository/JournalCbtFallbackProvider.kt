package com.example.mind_set.data.repository

object JournalCbtFallbackProvider {

    data class AnalysisResult(
        val aiResponse: String,
        val status: String
    )

    fun analyze(
        mood: String,
        triggerText: String,
        reframingText: String,
        tags: List<String>
    ): AnalysisResult {
        val lowerMood = mood.lowercase()
        val hasReframing = reframingText.isNotBlank()
        val tagString = if (tags.isNotEmpty()) tags.joinToString(", ") else "Tanpa Tag"

        val status = if (hasReframing || lowerMood.contains("tenang") || lowerMood.contains("semangat")) {
            "Tantangan Teratasi"
        } else {
            "Refleksi Harian"
        }

        val response = buildString {
            append("Respons CBT AI (Didukung Fallback Lokal): ")
            append("Merasakan '$mood' dengan pemicu [$tagString] adalah sinyal penting dari sistem sarafmu. ")
            if (hasReframing) {
                append("Upayamu merumuskan sudut pandang rasional ('$reframingText') menunjukkan kekuatan kognitif yang luar biasa dalam memutus siklus impuls otomatis.")
            } else {
                append("Cobalah luangkan 1 menit untuk menuliskan cara pandang alternatif yang lebih welas asih pada dirimu sendiri.")
            }
            append(" Terus catat kemajuan kecil ini untuk memperkuat jalur neuroplastisitas pemulihanmu.")
        }

        return AnalysisResult(aiResponse = response, status = status)
    }
}
