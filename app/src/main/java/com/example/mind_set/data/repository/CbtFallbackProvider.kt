package com.example.mind_set.data.repository

import com.example.mind_set.data.model.EmergencyIntervention

object CbtFallbackProvider {

    fun generate(
        urgeLevel: Int,
        triggerCategory: String,
        excludeTitle: String? = null
    ): EmergencyIntervention {
        val cleanTrigger = triggerCategory.substringAfter(" ").trim()
        val isHighUrgency = urgeLevel >= 4
        val urgencyStatus = when {
            urgeLevel >= 4 -> "HIGH"
            urgeLevel == 3 -> "MODERATE"
            else -> "LOW"
        }

        val candidates: List<Pair<String, List<String>>> = when {
            cleanTrigger.contains("Kesepian", ignoreCase = true) -> {
                if (isHighUrgency) listOf(
                    "Grounding Sosial & Regulasi Napas Cepat" to listOf(
                        "Jangan mengisolasi diri di kamar—pindah ke ruang tamu atau area terbuka sekarang.",
                        "Lakukan napas 4-7-8 sebanyak 3 siklus untuk menurunkan lonjakan stres.",
                        "Kirim pesan singkat (sekadar menyapa) ke teman dekat atau keluarga."
                    ),
                    "Intervensi Koneksi Nyata (Anti-Isolasi)" to listOf(
                        "Letakkan ponsel di mode pesawat selama 10 menit dan keluar menghirup udara segar.",
                        "Basuh wajah dengan air dingin untuk memicu refleks tenang sistem saraf.",
                        "Ingat: rasa sepi butuh koneksi manusia nyata, bukan pelarian digital sesaat."
                    ),
                    "Eskalasi Fisik: Ubah Lingkungan Total" to listOf(
                        "Tinggalkan perangkatmu di meja, lalu berjalan keluar rumah atau ke teras selama 5 menit.",
                        "Lakukan peregangan tubuh atau 15 kali squat untuk mengalihkan energi impulsif.",
                        "Hubungi teman bicara atau aktifkan audio percakapan/podcast yang menenangkan."
                    )
                ) else listOf(
                    "Behavioral Activation: Koneksi Positif" to listOf(
                        "Hubungi teman untuk mengobrol ringan atau diskusi topik positif selama 5–10 menit.",
                        "Putar musik instrumental atau podcast yang menenangkan di ruangan.",
                        "Catat perasaan kesepianmu di tab Jurnal Harian tanpa menghakimi diri sendiri."
                    ),
                    "Reframing Rasa Sepi (CBT)" to listOf(
                        "Bedakan antara 'sendirian secara fisik' dan 'kesepian secara emosional'.",
                        "Luangkan 5 menit membuat minuman hangat atau merapikan meja.",
                        "Fokus pada satu aktivitas kecil yang memberi rasa pencapaian hari ini."
                    )
                )
            }

            cleanTrigger.contains("Stres", ignoreCase = true) -> {
                if (isHighUrgency) listOf(
                    "De-eskalasi Stres Akut (Physiological Sigh)" to listOf(
                        "Tutup laptop dan jauhkan ponsel dari tangan selama 3 menit.",
                        "Lakukan Physiological Sigh: tarik napas 2x berturut-turut lewat hidung, embuskan panjang lewat mulut (ulangi 4x).",
                        "Pecah tugas yang membuatmu tertekan menjadi 1 langkah mikro berdurasi 5 menit saja."
                    ),
                    "Reset Fokus & Pemutusan Siklus Panik" to listOf(
                        "Berdiri dari kursi, lakukan peregangan bahu dan leher selama 60 detik.",
                        "Minum segelas penuh air putih dingin untuk menyegarkan fokus.",
                        "Katakan pada diri sendiri: 'Menghindari stres lewat konten negatif hanya menambah beban baru.'"
                    ),
                    "Mammalian Dive Reflex (Penurun Detak Jantung)" to listOf(
                        "Basuh wajah dan area sekitar mata dengan air dingin selama 15–30 detik.",
                        "Duduk tegak dengan kedua telapak kaki menapak penuh di lantai.",
                        "Tulis 1 kalimat di kertas tentang hal paling bikin stres saat ini agar keluar dari kepala."
                    )
                ) else listOf(
                    "Cognitive Restructuring: Manajemen Tekanan" to listOf(
                        "Tuliskan 3 hal paling mendesak di kertas, lalu kerjakan yang paling mudah dulu.",
                        "Gunakan teknik Pomodoro: fokus 15 menit, istirahat tanpa layar 3 menit.",
                        "Evaluasi pikiran otomatis—apakah tugas ini benar-benar harus sempurna sekarang?"
                    ),
                    "Micro-Break Terarah" to listOf(
                        "Alihkan pandangan ke objek berjarak 6 meter selama 20 detik (aturan 20-20-20).",
                        "Rapikan meja kerjamu selama 2 menit untuk mengembalikan rasa kendali.",
                        "Lanjutkan kembali dengan target kecil yang realistis."
                    )
                )
            }

            cleanTrigger.contains("Bosan", ignoreCase = true) -> {
                if (isHighUrgency) listOf(
                    "Pemutusan Pola Impulsif (Pattern Interrupt)" to listOf(
                        "Segera kunci layar ponsel dan taruh di ruangan lain selama 10 menit.",
                        "Lakukan Grounding 5-4-3-2-1: sebutkan 5 benda yang terlihat & 4 yang bisa disentuh.",
                        "Ganti stimulasi pasif dengan aktivitas fisik cepat (push-up atau jalan keliling rumah)."
                    ),
                    "Aturan Tunda 15 Menit (Urge Surfing)" to listOf(
                        "Bayangkan dorongan ini seperti gelombang laut—ia naik, memuncak, lalu pasti surut.",
                        "Setel timer 15 menit dan sibukkan tanganmu (merapikan barang atau mencuci muka).",
                        "Setelah 15 menit, nilai ulang seberapa kuat dorongan tersebut."
                    )
                ) else listOf(
                    "Substitusi Dopamin Sehat" to listOf(
                        "Ganti scrolling pasif dengan aktivitas aktif: baca buku fisik atau aktivitas hobi.",
                        "Jika sudah larut malam, aktifkan mode grayscale/tidur pada layar ponsel.",
                        "Siapkan rutinitas sebelum tidur agar otak tidak mencari stimulasi berlebih."
                    ),
                    "Eksplorasi Aktivitas Alternatif" to listOf(
                        "Buat teh hangat atau camilan ringan di dapur untuk memutus kebosanan.",
                        "Dengarkan playlist favorit sambil merencanakan target besok pagi.",
                        "Ingat kembali pencapaian Clean Streak yang sudah kamu bangun sejauh ini."
                    )
                )
            }

            cleanTrigger.contains("Visual", ignoreCase = true) -> listOf(
                "Protokol Blokir Stimulus Visual Cepat" to listOf(
                    "Segera tutup aplikasi/tab pemicu dan kunci layar ponsel sekarang juga.",
                    "Alihkan pandangan ke arah jendela atau objek nyata di ruangan selama 60 detik.",
                    "Tarik napas dalam 4 detik, embuskan 6 detik untuk menurunkan respons dopamin impulsif."
                ),
                "Detoks Layar & Reset Lingkungan" to listOf(
                    "Letakkan ponsel menghadap ke bawah atau pindah ke ruangan lain selama 10 menit.",
                    "Cuci wajah dengan air dingin untuk menghentikan reaksi otomatis otak.",
                    "Aktifkan filter konten/SafeSearch agar pemicu tidak muncul kembali di beranda."
                )
            )

            else -> listOf(
                "Pemulihan Energi & Proteksi Diri" to listOf(
                    "Dorongan ini muncul karena otakmu kelelahan dan mencari jalan pintas dopamin.",
                    "Jauhkan ponsel dari tempat tidur (charge di meja yang jauh dari jangkauan tangan).",
                    "Lakukan relaksasi otot progresif selama 2 menit lalu segera beristirahat/tidur."
                ),
                "Reframing Kelelahan Mental" to listOf(
                    "Akui bahwa tubuhmu butuh istirahat nyata, bukan stimulasi layar tambahan.",
                    "Minum air putih, matikan lampu utama, dan dengarkan suara alam/white noise.",
                    "Tidur lebih awal adalah investasi terbaik untuk menjaga Clean Streak esok hari."
                )
            )
        }

        // Filter agar tidak mengulang judul teknik yang baru saja gagal/ditolak user
        val filteredCandidates = if (excludeTitle != null && candidates.size > 1) {
            candidates.filterNot { it.first == excludeTitle }
        } else {
            candidates
        }

        val (selectedTitle, selectedSteps) = filteredCandidates.random()

        return EmergencyIntervention(
            urgeLevel = urgeLevel,
            triggerCategory = triggerCategory,
            urgencyStatus = urgencyStatus,
            cbtTechniqueTitle = selectedTitle,
            recommendationSteps = selectedSteps,
            sourceType = "LOCAL_RULE_BASED"
        )
    }
}