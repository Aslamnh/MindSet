package com.example.mind_set.data.progress

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Logika murni (tanpa Android/DB) untuk menghitung streak, rasio, dan konsistensi.
 * Dibuat terpisah agar mudah diuji (unit test).
 */
object ProgressCalculator {

    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun todayString(): String = fmt.format(Date())

    fun parseDate(s: String): Date? = try { fmt.parse(s) } catch (_: Exception) { null }

    fun addDays(dateStr: String, days: Int): String {
        val cal = Calendar.getInstance()
        val d = parseDate(dateStr) ?: return dateStr
        cal.time = d
        cal.add(Calendar.DAY_OF_MONTH, days)
        return fmt.format(cal.time)
    }

    fun isConsecutive(prev: String, next: String): Boolean =
        addDays(prev, 1) == next

    /**
     * Streak saat ini = jumlah hari berturut-turut SUCCESS yang berakhir
     * pada check-in terakhir (mundur hari demi hari).
     */
    fun currentStreak(checkIns: List<DailyCheckIn>): Int {
        if (checkIns.isEmpty()) return 0
        val byDate = checkIns.associateBy { it.date }
        val latest = checkIns.maxByOrNull { it.date }?.date ?: return 0
        if (byDate[latest]?.status != CheckInStatus.SUCCESS) return 0

        var count = 0
        var cursor = latest
        while (true) {
            val ci = byDate[cursor]
            if (ci?.status == CheckInStatus.SUCCESS) {
                count++
                val prev = addDays(cursor, -1)
                if (!byDate.containsKey(prev)) break
                cursor = prev
            } else break
        }
        return count
    }

    /**
     * Streak terpanjang sepanjang riwayat (run terpanjang SUCCESS berturut-turut).
     */
    fun longestStreak(checkIns: List<DailyCheckIn>): Int {
        if (checkIns.isEmpty()) return 0
        var best = 0
        var cur = 0
        var prevDate: String? = null
        for (ci in checkIns.sortedBy { it.date }) {
            if (ci.status == CheckInStatus.SUCCESS) {
                cur = if (prevDate != null && ci.date == addDays(prevDate, 1)) cur + 1 else 1
                if (cur > best) best = cur
                prevDate = ci.date
            } else {
                cur = 0
                prevDate = ci.date
            }
        }
        return best
    }

    fun successRatio(successDays: Int, totalDays: Int): Float =
        if (totalDays <= 0) 0f else successDays.toFloat() / totalDays.toFloat()

    /**
     * Konsistensi 7 hari terakhir (0..1).
     */
    fun weeklyConsistency(checkIns: List<DailyCheckIn>, today: String): Float {
        val byDate = checkIns.associateBy { it.date }
        var success = 0
        for (i in 0 until 7) {
            val d = addDays(today, -i)
            if (byDate[d]?.status == CheckInStatus.SUCCESS) success++
        }
        return success.toFloat() / 7f
    }

    /**
     * Milestone berikutnya dan progres (0..1) menuju milestone tersebut.
     * Ambang CBT: 3, 7, 21, 90 hari.
     */
    fun nextMilestone(currentStreak: Int): Pair<Int, Float> {
        val targets = listOf(3, 7, 21, 90)
        val next = targets.firstOrNull { it > currentStreak } ?: 90
        val prev = targets.lastOrNull { it <= currentStreak } ?: 0
        val span = (next - prev).coerceAtLeast(1)
        val progress = ((currentStreak - prev).toFloat() / span.toFloat()).coerceIn(0f, 1f)
        return next to progress
    }

    /**
     * Pesan apresiasi adaptif berbasis streak.
     */
    fun appreciationMessage(currentStreak: Int): String = when {
        currentStreak >= 90 -> "Luar biasa! Kamu telah membangun ketahanan otomatis. Pertahankan ritmemu."
        currentStreak >= 21 -> "Konsistensimu membentuk jalur saraf baru yang lebih sehat. Fase pola baru terkunci."
        currentStreak >= 7 -> "Kesadaran dirimu tumbuh. Terus kenali pemicu dan jaga ritme."
        currentStreak >= 3 -> "Langkah kecil yang konsisten adalah kemenangan nyata. Teruskan!"
        currentStreak >= 1 -> "Awal yang baik. Setiap hari bersih adalah pencapaian nyata."
        else -> "Hari ini adalah kesempatan baru. Mulai lagi tanpa menghakimi diri sendiri."
    }

    /**
     * Rekomendasi pemulihan saat kambuh (compassionate, tanpa stigma).
     */
    fun recoveryRecommendation(successRatio: Float): String = when {
        successRatio >= 0.8f ->
            "Satu kejadian tidak menghapus puluhan hari kemenanganmu. Rasio suksesmu masih ${(successRatio * 100).toInt()}%. " +
                "Catat pemicunya, lalu coba langkah grounding 5-4-3-2-1."
        successRatio >= 0.5f ->
            "Pemulihan berbentuk spiral, bukan garis lurus. Evaluasi pemicu, istirahat cukup, dan mulai lagi besok dengan target kecil."
        else ->
            "Jangan hakimi dirimu. Fokus pada satu hari ke depan saja. Coba latihan pernapasan 4-4-4-4 dan hubungi teman pendamping bila perlu."
    }
}