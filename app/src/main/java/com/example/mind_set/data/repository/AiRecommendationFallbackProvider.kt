package com.example.mind_set.data.repository

import com.example.mind_set.data.model.AiRecommendation
import com.example.mind_set.data.model.UserContextSummary

object AiRecommendationFallbackProvider {

    fun generate(context: UserContextSummary): AiRecommendation {
        val moodLower = context.currentMood.lowercase()
        val triggersJoined = context.dominantTriggers.joinToString(" ").lowercase()
        val journalLower = context.recentJournalSummary.lowercase()
        val emergencyLower = context.recentEmergencySummary.lowercase()

        val isHighRisk = context.totalEmergencySessions >= 3 ||
            emergencyLower.contains("level 4") ||
            emergencyLower.contains("level 5") ||
            moodLower.contains("cemas") ||
            context.successRatioPercent < 70.0

        val warningMessage = when {
            emergencyLower.contains("level 4") || emergencyLower.contains("level 5") ->
                "Terdeteksi lonjakan dorongan intensitas tinggi pada riwayat terbaru. Prioritaskan jarak fisik dari pemicu digital malam ini."
            moodLower.contains("cemas") ->
                "Pola emosi cemas terdeteksi. Jangan hadapi sendirian—lakukan regulasi napas sebelum membuka media sosial."
            context.successRatioPercent < 70.0 ->
                "Rasio keberhasilan sedang menurun, namun setiap hari baru adalah kesempatan memperkuat kendali diri."
            else -> null
        }

        return when {
            moodLower.contains("cemas") || emergencyLower.contains("level 4") || emergencyLower.contains("level 5") -> {
                AiRecommendation(
                    title = "Pengalihan Fokus: Grounding 5-4-3-2-1",
                    description = "Berdasarkan mood dan riwayat pemicumu, teknik grounding sensorik membantu menurunkan aktivasi kecemasan dan memutus dorongan impulsif dalam 5 menit.",
                    activityMethod = "Metode 5-4-3-2-1 • Grounding Sensorik",
                    durationLabel = "5 Menit",
                    actionSteps = listOf(
                        "Sebutkan 5 benda yang terlihat di sekitarmu saat ini.",
                        "Sentuh 4 tekstur berbeda di dekatmu dan rasakan suhunya.",
                        "Dengarkan 3 suara di sekitar, hirup 2 aroma, dan tarik napas 4-4-4-4."
                    ),
                    habitAvoidanceTip = "Letakkan ponsel di luar jangkauan tangan selama 15 menit saat dorongan cemas muncul.",
                    warningMessage = warningMessage,
                    isHighRiskPattern = isHighRisk,
                    sourceType = "RULE_BASED_FALLBACK"
                )
            }

            triggersJoined.contains("malam") || triggersJoined.contains("bosan") || journalLower.contains("malam") -> {
                AiRecommendation(
                    title = "Ritual Malam Bebas Layar & Baca Fisik",
                    description = "Pola catatanmu menunjukkan kerentanan saat malam hari atau waktu senggang. Mengganti layar dengan aktivitas fisik ringan mencegah kebiasaan buruk berulang.",
                    activityMethod = "Behavioral Substitution • CBT",
                    durationLabel = "10 Menit",
                    actionSteps = listOf(
                        "Aktifkan mode tidur / grayscale pada ponsel 30 menit sebelum tidur.",
                        "Siapkan buku fisik atau jurnal kertas di samping tempat tidur.",
                        "Minum segelas air putih dan lakukan peregangan bahu ringan."
                    ),
                    habitAvoidanceTip = "Hindari membawa smartphone ke atas kasur agar otak tidak mengasosiasikan waktu tidur dengan scrolling.",
                    warningMessage = warningMessage,
                    isHighRiskPattern = isHighRisk,
                    sourceType = "RULE_BASED_FALLBACK"
                )
            }

            triggersJoined.contains("stres") || journalLower.contains("tugas") || journalLower.contains("kerja") -> {
                AiRecommendation(
                    title = "Micro-Break & Physiological Sigh",
                    description = "Tekanan tugas/kerja sering memicu pelarian ke konten negatif. Jeda terstruktur membantu otak melakukan reset fokus tanpa rasa bersalah.",
                    activityMethod = "Physiological Sigh + Pomodoro 15m",
                    durationLabel = "3 Menit",
                    actionSteps = listOf(
                        "Tarik napas 2x berturut-turut melalui hidung, lalu embuskan panjang lewat mulut (ulangi 4x).",
                        "Pecah pekerjaan yang terasa berat menjadi 1 tugas mikro berdurasi 10 menit.",
                        "Berdiri dan berjalan keluar ruangan selama 2 menit untuk menyegarkan pandangan."
                    ),
                    habitAvoidanceTip = "Tutup tab atau aplikasi hiburan sebelum memulai sesi fokus 15 menit.",
                    warningMessage = warningMessage,
                    isHighRiskPattern = isHighRisk,
                    sourceType = "RULE_BASED_FALLBACK"
                )
            }

            triggersJoined.contains("kesepian") || triggersJoined.contains("sendirian") -> {
                AiRecommendation(
                    title = "Aktivasi Koneksi Sosial Nyata",
                    description = "Rasa sepi kerap memicu pencarian dopamin instan secara digital. Menghubungi orang terdekat memberi rasa aman yang lebih sehat.",
                    activityMethod = "Behavioral Activation • Sosial",
                    durationLabel = "5 Menit",
                    actionSteps = listOf(
                        "Pindah dari kamar tertutup ke ruang keluarga atau area terbuka.",
                        "Kirim pesan singkat ke teman atau keluarga sekadar menanyakan kabar.",
                        "Putar audio podcast atau musik instrumental yang menenangkan."
                    ),
                    habitAvoidanceTip = "Saat merasa sendirian, hindari membuka timeline media sosial tanpa tujuan jelas.",
                    warningMessage = warningMessage,
                    isHighRiskPattern = isHighRisk,
                    sourceType = "RULE_BASED_FALLBACK"
                )
            }

            else -> {
                AiRecommendation(
                    title = "Penguatan Konsistensi: Refleksi Positif",
                    description = "Streak ${context.currentStreakDays} hari dan rasio ${"%.1f".format(context.successRatioPercent)}% menunjukkan progres positif. Pertahankan momentum dengan kebiasaan sehat.",
                    activityMethod = "Cognitive Reframing • CBT Harian",
                    durationLabel = "5 Menit",
                    actionSteps = listOf(
                        "Tuliskan 1 pencapaian kecil yang berhasil kamu jaga hari ini.",
                        "Lakukan pernapasan tenang 4-4-4-4 selama 2 menit.",
                        "Rencanakan 1 aktivitas positif untuk waktu luangmu sore/malam nanti."
                    ),
                    habitAvoidanceTip = "Tetapkan batas waktu layar harian dan isi waktu luang dengan aktivitas fisik atau hobi offline.",
                    warningMessage = warningMessage,
                    isHighRiskPattern = isHighRisk,
                    sourceType = "RULE_BASED_FALLBACK"
                )
            }
        }
    }
}
