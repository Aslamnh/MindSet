package com.example.mind_set.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToLong

/**
 * Helper tanggal berbasis [java.util.Calendar] agar tetap aman untuk minSdk 24
 * (java.time.* baru tersedia mulai API 26).
 *
 * Semua "date key" memakai format ISO `yyyy-MM-dd` sehingga perbandingan
 * string secara leksikografis setara dengan perbandingan tanggal.
 */
object DateUtils {

    private val keyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("id-ID"))
    private val shortDateFormat = SimpleDateFormat("d MMM yyyy", Locale.forLanguageTag("id-ID"))

    /** Header hari kalender, dimulai dari Minggu. */
    val dayHeaders = listOf("S", "S", "R", "K", "J", "S", "M")

    fun todayKey(): String = keyOf(System.currentTimeMillis())

    fun keyOf(millis: Long): String = synchronized(keyFormat) {
        keyFormat.format(Date(millis))
    }

    fun formatShortDate(key: String): String {
        val millis = millisOf(key) ?: return key
        return synchronized(shortDateFormat) { shortDateFormat.format(Date(millis)) }
    }

    fun daysBetween(startKey: String, endKey: String): Int =
        (dayNumber(endKey) - dayNumber(startKey)).toInt()

    fun shiftDay(key: String, delta: Int): String {
        val calendar = Calendar.getInstance()
        calendar.time = Date(millisOf(key) ?: System.currentTimeMillis())
        calendar.add(Calendar.DAY_OF_MONTH, delta)
        return keyOf(calendar.timeInMillis)
    }

    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH)

    fun monthLabel(year: Int, month: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month, 1, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return synchronized(monthFormat) { monthFormat.format(calendar.time) }
    }

    fun daysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply { set(year, month, 1, 12, 0, 0) }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /** Indeks hari pertama bulan (0 = Minggu ... 6 = Sabtu). */
    fun firstWeekdayIndex(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance().apply { set(year, month, 1, 12, 0, 0) }
        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }

    fun dayKey(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(year, month, day, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return keyOf(calendar.timeInMillis)
    }

    fun shiftMonth(year: Int, month: Int, delta: Int): Pair<Int, Int> {
        val calendar = Calendar.getInstance().apply { set(year, month, 1, 12, 0, 0) }
        calendar.add(Calendar.MONTH, delta)
        return calendar.get(Calendar.YEAR) to calendar.get(Calendar.MONTH)
    }

    /** True jika (year, month) berada setelah (otherYear, otherMonth). */
    fun isAfterMonth(year: Int, month: Int, otherYear: Int, otherMonth: Int): Boolean =
        year > otherYear || (year == otherYear && month > otherMonth)

    private fun millisOf(key: String): Long? = try {
        synchronized(keyFormat) { keyFormat.parse(key)?.time }
    } catch (_: Exception) {
        null
    }

    private fun dayNumber(key: String): Long {
        val calendar = Calendar.getInstance()
        calendar.time = Date(millisOf(key) ?: System.currentTimeMillis())
        // Pakai tengah hari supaya pergeseran DST maksimal 1 jam tidak mengubah hasil pembulatan.
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return (calendar.timeInMillis / 86_400_000.0).roundToLong()
    }
}
