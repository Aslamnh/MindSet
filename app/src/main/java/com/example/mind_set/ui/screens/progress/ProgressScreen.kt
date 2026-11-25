package com.example.mind_set.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.data.progress.*
import com.example.mind_set.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ProgressScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = remember { ProgressRepository.get(context) }
    val scope = rememberCoroutineScope()
    val state by repo.state.collectAsState()

    LaunchedEffect(Unit) { repo.initialize() }

    // Kalender bulan berjalan
    val nowCal = remember { Calendar.getInstance() }
    val year = nowCal.get(Calendar.YEAR)
    val month = nowCal.get(Calendar.MONTH) // 0-based
    val monthLabel = SimpleDateFormat("MMMM yyyy", Locale("id")).format(nowCal.time)
    val calendarDays = remember(state.checkIns, year, month) {
        buildCalendarDays(year, month, state.checkIns)
    }
    val todayStr = ProgressCalculator.todayString()
    val (nextTarget, milestoneProgress) = ProgressCalculator.nextMilestone(state.currentStreak)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindSetBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Mind-Set", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Text("Analitik Pemulihan CBT", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = MindSetNeutralGrey)
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Statistik & Pemulihan CBT", style = MaterialTheme.typography.headlineSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Perjalanan bukan tentang kesempurnaan, tapi ketahanan untuk terus maju.", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)

            Spacer(Modifier.height(16.dp))

            // ── Stats row ─────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    emoji = "🔥",
                    label = "Hari Bersih",
                    value = "${state.currentStreak} Hari",
                    sub = "Terpanjang: ${state.longestStreak} Hari",
                    valueColor = MindSetNeutral
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    emoji = "✅",
                    label = "Rasio Sukses",
                    value = "${(state.successRatio * 100).toInt()}%",
                    sub = "${state.successDays}/${state.totalDays} hari",
                    valueColor = MindSetTertiary
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Setiap hari bersih adalah pencapaian nyata bagi sistem sarafmu untuk menyusun ulang pola ketahanan baru.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            // ── Today status card ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Status Hari Ini", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    if (state.todayStatus == null) {
                        Text("Belum mencatat hari ini. Tandai statusmu:", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { scope.launch { repo.recordOutcome(todayStr, CheckInStatus.SUCCESS) } },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MindSetTertiary)
                            ) {
                                Text("✅ Berhasil menahan", color = Color.White, style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { scope.launch { repo.recordOutcome(todayStr, CheckInStatus.RELAPSE, 4, null, "Kekambuhan hari ini") } },
                                modifier = Modifier.weight(1f).height(44.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary)
                            ) {
                                Text("💙 Catat kekambuhan", color = MindSetNeutral, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    } else {
                        val label = if (state.todayStatus == CheckInStatus.SUCCESS) "✅ Berhasil menahan godaan" else "💙 Mengalami kekambuhan"
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(ProgressCalculator.appreciationMessage(state.currentStreak), style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { scope.launch { repo.recordOutcome(todayStr, CheckInStatus.SUCCESS) } },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MindSetDivider)
                            ) { Text("Ubah ke Berhasil", style = MaterialTheme.typography.labelSmall) }
                            OutlinedButton(
                                onClick = { scope.launch { repo.recordOutcome(todayStr, CheckInStatus.RELAPSE, 4, null, "Kekambuhan hari ini") } },
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MindSetDivider)
                            ) { Text("Ubah ke Kambuh", style = MaterialTheme.typography.labelSmall) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Calendar heatmap ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("IKHTISAR KONSISTENSI", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                    Text(monthLabel, style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))

                    val dayHeaders = listOf("M", "S", "S", "R", "K", "J", "S")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayHeaders.forEach { h ->
                            Text(h, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val weeks = calendarDays.chunked(7)
                    weeks.forEach { week ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            val padded = week + List(7 - week.size) { CalendarDay(0, DayStatus.EMPTY) }
                            padded.forEach { dayItem ->
                                Box(
                                    modifier = Modifier.weight(1f).aspectRatio(1f).padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when (dayItem.status) {
                                                DayStatus.CLEAN -> MindSetPrimary
                                                DayStatus.RELAPSE -> MindSetError.copy(alpha = 0.7f)
                                                DayStatus.SKIP -> MindSetNeutralLight.copy(alpha = 0.4f)
                                                DayStatus.FUTURE -> MindSetDivider
                                                DayStatus.EMPTY -> Color.Transparent
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (dayItem.day > 0) {
                                        Text(
                                            text = "${dayItem.day}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = when (dayItem.status) {
                                                DayStatus.CLEAN, DayStatus.RELAPSE -> Color.White
                                                else -> MindSetNeutralGrey
                                            },
                                            fontWeight = if (dayItem.day == nowCal.get(Calendar.DAY_OF_MONTH)) FontWeight.ExtraBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Konsistensi Minggu Ini: ${(state.weeklyConsistency * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Compassionate relapse card ─────────────────────────
            if (state.relapseDays > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🤗", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text("Mengalami Kekambuhan?", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            ProgressCalculator.recoveryRecommendation(state.successRatio),
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutralGrey
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Rasio sukses masih ${(state.successRatio * 100).toInt()}%! Satu kejadian tidak menghapus puluhan hari kemenanganmu.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = { /* evaluasi kambuh dicatat lewat Status Hari Ini */ },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary)
                        ) {
                            Text("💙 Catat Evaluasi Kambuh (Bebas Stigma)", color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // ── CBT Phases ────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pencapaian & Fase CBT", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                        Surface(shape = RoundedCornerShape(10.dp), color = MindSetPrimaryContainer) {
                            Text(
                                if (state.currentStreak >= 90) "Fase 4 Selesai" else "Fase 3 Aktif",
                                style = MaterialTheme.typography.labelSmall,
                                color = MindSetPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    val phases = listOf(
                        PhaseData("Fase 1: 3 Hari Detoks", "Pemutusan impuls otomatis", (state.currentStreak.coerceIn(0, 3) / 3f), state.currentStreak >= 3, false),
                        PhaseData("Fase 2: 7 Hari Kesadaran Diri", "Mengenali pola pemicu kognitif", (state.currentStreak.coerceIn(0, 7) / 7f), state.currentStreak >= 7, state.currentStreak < 3),
                        PhaseData("Fase 3: 21 Hari Pola Baru", "Fase Berlangsung (${state.currentStreak}/21 • ${(state.currentStreak.coerceIn(0, 21) / 21f * 100).toInt()}%)", (state.currentStreak.coerceIn(0, 21) / 21f), state.currentStreak >= 21, state.currentStreak < 7),
                        PhaseData("Fase 4: 90 Hari Ketahanan Otomatis", "Gaya hidup stabil & autonomi penuh", 0f, false, state.currentStreak < 21)
                    )

                    phases.forEach { phase ->
                        PhaseItem(phase)
                        if (phase != phases.last()) {
                            Box(modifier = Modifier.padding(start = 14.dp).width(2.dp).height(12.dp).background(MindSetDivider))
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Tersisa ${nextTarget - state.currentStreak} hari untuk mencapai milestone $nextTarget hari. Progres milestone: ${(milestoneProgress * 100).toInt()}%.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Affirmation ───────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Text("🌟", fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Pemberitahuan Welas Asih", style = MaterialTheme.typography.labelMedium, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            ProgressCalculator.appreciationMessage(state.currentStreak),
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutral
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun buildCalendarDays(year: Int, month: Int, checkIns: List<DailyCheckIn>): List<CalendarDay> {
    val byDate = checkIns.associateBy { it.date }
    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    val firstDow = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val empties = firstDow - Calendar.SUNDAY
    val result = mutableListOf<CalendarDay>()
    repeat(empties.coerceAtLeast(0)) { result.add(CalendarDay(0, DayStatus.EMPTY)) }
    val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayStr = ProgressCalculator.todayString()
    for (day in 1..lastDay) {
        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
        val status = when {
            byDate[dateStr]?.status == CheckInStatus.SUCCESS -> DayStatus.CLEAN
            byDate[dateStr]?.status == CheckInStatus.RELAPSE -> DayStatus.RELAPSE
            byDate[dateStr]?.status == CheckInStatus.SKIP -> DayStatus.SKIP
            dateStr > todayStr -> DayStatus.FUTURE
            else -> DayStatus.SKIP
        }
        result.add(CalendarDay(day, status))
    }
    return result
}

data class PhaseData(
    val title: String,
    val subtitle: String,
    val progress: Float,
    val completed: Boolean,
    val locked: Boolean
)

@Composable
fun StatCard(modifier: Modifier, emoji: String, label: String, value: String, sub: String, valueColor: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
            Text(value, style = MaterialTheme.typography.titleLarge, color = valueColor, fontWeight = FontWeight.Bold)
            Text(sub, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
        }
    }
}

@Composable
fun PhaseItem(phase: PhaseData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        phase.completed -> MindSetTertiary
                        phase.locked -> MindSetNeutralLight.copy(alpha = 0.3f)
                        else -> MindSetPrimary
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                phase.completed -> Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                phase.locked -> Icon(Icons.Filled.Lock, contentDescription = null, tint = MindSetNeutralGrey, modifier = Modifier.size(14.dp))
                else -> Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(phase.title, style = MaterialTheme.typography.labelMedium, color = if (phase.locked) MindSetNeutralGrey else MindSetNeutral, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                if (phase.completed) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MindSetTertiaryLight) {
                        Text("Selesai ✓", style = MaterialTheme.typography.labelSmall, color = MindSetTertiary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                } else if (phase.locked) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MindSetNeutralLight.copy(alpha = 0.2f)) {
                        Text("Terkunci", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(phase.subtitle, style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
            if (!phase.completed && !phase.locked) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { phase.progress },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MindSetPrimary,
                    trackColor = MindSetDivider
                )
                Spacer(Modifier.height(2.dp))
                Text("${(phase.progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MindSetPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}