package com.example.mind_set.ui.emergency

import com.example.mind_set.data.model.EmergencyIntervention

/**
 * Alur UI Emergency (Pause Ground):
 *
 * Phase 1 – Panduan CBT (retryCount < 3):
 *   Tombol "Belum Mempan? Coba Panduan Lain" merotasi saran dari CbtFallbackProvider.
 *
 * Phase 2 – Refleksi Detail Krisis (retryCount >= 3):
 *   isReflectionPhase = true → Muncul TextField terbuka untuk menceritakan kondisi.
 *
 * Phase 3 – Pengiriman Final & Penutup:
 *   isSessionCompleted = true → Pesan Penutup CBT Empatik + tombol kembali ke Beranda.
 */
data class EmergencyUiState(
    val urgeLevel: Int = 3,
    val selectedTrigger: String = "😔 Kesepian",
    val additionalNote: String = "",
    val isLoading: Boolean = false,
    val currentIntervention: EmergencyIntervention? = null,
    val emergencyHistory: List<EmergencyIntervention> = emptyList(),
    val retryCount: Int = 0,
    val errorMessage: String? = null,

    // Phase 2: Refleksi Detail Krisis
    val isReflectionPhase: Boolean = false,
    val reflectionText: String = "",

    // Phase 3: Penutup Sesi
    val isSessionCompleted: Boolean = false,
    val closingMessage: String = "",
    val finalLogId: Int? = null
)
