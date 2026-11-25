package com.example.mind_set

import com.example.mind_set.data.progress.CheckInStatus
import com.example.mind_set.data.progress.DailyCheckIn
import com.example.mind_set.data.progress.ProgressCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressCalculatorTest {

    @Test
    fun currentStreak_countsConsecutiveSuccesses() {
        val checkIns = listOf(
            DailyCheckIn("2026-11-20", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-21", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-22", CheckInStatus.SUCCESS)
        )
        assertEquals(3, ProgressCalculator.currentStreak(checkIns))
    }

    @Test
    fun currentStreak_resetsOnRelapse() {
        val checkIns = listOf(
            DailyCheckIn("2026-11-20", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-21", CheckInStatus.RELAPSE),
            DailyCheckIn("2026-11-22", CheckInStatus.SUCCESS)
        )
        assertEquals(1, ProgressCalculator.currentStreak(checkIns))
    }

    @Test
    fun currentStreak_stopsOnGap() {
        val checkIns = listOf(
            DailyCheckIn("2026-11-20", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-22", CheckInStatus.SUCCESS)
        )
        assertEquals(1, ProgressCalculator.currentStreak(checkIns))
    }

    @Test
    fun longestStreak_findsMaxRun() {
        val checkIns = listOf(
            DailyCheckIn("2026-11-18", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-19", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-20", CheckInStatus.RELAPSE),
            DailyCheckIn("2026-11-21", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-22", CheckInStatus.SUCCESS),
            DailyCheckIn("2026-11-23", CheckInStatus.SUCCESS)
        )
        assertEquals(3, ProgressCalculator.longestStreak(checkIns))
    }

    @Test
    fun successRatio_handlesEmpty() {
        assertEquals(0f, ProgressCalculator.successRatio(0, 0))
    }

    @Test
    fun successRatio_computes() {
        assertEquals(0.8f, ProgressCalculator.successRatio(8, 10), 0.001f)
    }

    @Test
    fun weeklyConsistency_countsLast7Days() {
        val checkIns = (0 until 7).map { i ->
            DailyCheckIn(ProgressCalculator.addDays("2026-11-23", -i), CheckInStatus.SUCCESS)
        }
        assertEquals(1f, ProgressCalculator.weeklyConsistency(checkIns, "2026-11-23"), 0.001f)
    }

    @Test
    fun nextMilestone_returnsNextTargetAndProgress() {
        val (target, progress) = ProgressCalculator.nextMilestone(14)
        assertEquals(21, target)
        assertTrue(progress in 0f..1f)
    }

    @Test
    fun addDays_works() {
        assertEquals("2026-11-24", ProgressCalculator.addDays("2026-11-23", 1))
        assertEquals("2026-11-22", ProgressCalculator.addDays("2026-11-23", -1))
    }
}