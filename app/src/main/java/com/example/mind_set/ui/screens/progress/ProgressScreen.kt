package com.example.mind_set.ui.screens.progress

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.data.model.CalendarDay
import com.example.mind_set.data.model.CbtPhaseInfo
import com.example.mind_set.data.model.DayStatus
import com.example.mind_set.data.model.ProgressSummary
import com.example.mind_set.ui.theme.*
import com.example.mind_set.util.DateUtils
import java.util.Locale

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    val summary = uiState.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindSetBackground)
            .verticalScroll(rememberScrollState())
    ) {
        ProgressTopBar()

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Statistik & Pemulihan CBT",
                style = MaterialTheme.typography.headlineSmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Perjalanan bukan tentang kesempurnaan, tapi ketahanan untuk terus maju.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.LocalFireDepartment,
                    label = "Hari Bersih",
                    value = "${summary.currentCleanStreak} Hari",
                    sub = "Terpanjang: ${summary.longestCleanStreak} Hari",
                    valueColor = MindSetNeutral
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.CheckCircle,
                    label = "Rasio Sukses",
                    value = formatPercent(summary.successRate),
                    sub = "${summary.totalCleanDays}/${summary.totalTrackedDays} hari bersih",
                    valueColor = MindSetTertiary
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = "Streak Jurnal",
                    value = "${summary.currentJournalStreak} Hari",
                    sub = "Konsistensi refleksi",
                    valueColor = MindSetPrimary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Insights,
                    label = "Konsistensi",
                    value = formatPercent(summary.consistencyRate),
                    sub = "${summary.activeDaysThisMonth}/${summary.elapsedDaysThisMonth} hari bulan ini",
                    valueColor = MindSetSecondary
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Setiap hari bersih adalah pencapaian nyata bagi sistem sarafmu untuk menyusun ulang pola ketahanan baru.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(16.dp))

        CalendarCard(
            uiState = uiState,
            onPreviousMonth = viewModel::previousMonth,
            onNextMonth = viewModel::nextMonth
        )

        Spacer(Modifier.height(16.dp))

        RelapseCard(
            summary = summary,
            onLogRelapse = viewModel::openRelapseDialog
        )

        Spacer(Modifier.height(16.dp))

        PhasesCard(phases = summary.phases)

        Spacer(Modifier.height(16.dp))

        AffirmationCard(message = summary.affirmation)

        Spacer(Modifier.height(24.dp))
    }

    if (uiState.showRelapseDialog) {
        RelapseDialog(
            uiState = uiState,
            onDismiss = viewModel::closeRelapseDialog,
            onTriggerChange = viewModel::updateRelapseTrigger,
            onNoteChange = viewModel::updateRelapseNote,
            onConfirm = viewModel::saveRelapse
        )
    }
}

@Composable
private fun ProgressTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Mind-Set",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey
            )
            Text(
                text = "Analitik Pemulihan CBT",
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
        }
        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = null,
            tint = MindSetNeutralGrey
        )
    }
}

@Composable
private fun CalendarCard(
    uiState: ProgressUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val summary = uiState.summary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "IKHTISAR KONSISTENSI",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.monthLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onPreviousMonth) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Bulan sebelumnya",
                        tint = MindSetNeutralGrey
                    )
                }
                IconButton(onClick = onNextMonth, enabled = uiState.canGoNextMonth) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Bulan berikutnya",
                        tint = if (uiState.canGoNextMonth) MindSetNeutralGrey else MindSetNeutralLight
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateUtils.dayHeaders.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            uiState.calendarDays.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { dayItem ->
                        CalendarDayCell(
                            dayItem = dayItem,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LegendItem(color = MindSetTertiary, label = "Bersih")
                LegendItem(color = MindSetError.copy(alpha = 0.75f), label = "Kambuh")
                LegendItem(color = MindSetNeutralLight.copy(alpha = 0.4f), label = "Tanpa data")
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = MindSetTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Konsistensi Bulan Ini: ${formatPercent(summary.consistencyRate)} " +
                        "(${summary.activeDaysThisMonth}/${summary.elapsedDaysThisMonth} hari)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayItem: CalendarDay,
    modifier: Modifier = Modifier
) {
    val background = when (dayItem.status) {
        DayStatus.CLEAN -> MindSetTertiary
        DayStatus.RELAPSE -> MindSetError.copy(alpha = 0.75f)
        DayStatus.SKIP -> MindSetNeutralLight.copy(alpha = 0.35f)
        DayStatus.FUTURE -> MindSetDivider
        DayStatus.EMPTY -> Color.Transparent
    }
    val textColor = when (dayItem.status) {
        DayStatus.CLEAN, DayStatus.RELAPSE -> Color.White
        else -> MindSetNeutralGrey
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(background)
            .then(
                if (dayItem.isToday) {
                    Modifier.border(2.dp, MindSetPrimary, CircleShape)
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (dayItem.day > 0) {
            Text(
                text = "${dayItem.day}",
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = if (dayItem.isToday) FontWeight.ExtraBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey
        )
    }
}

@Composable
private fun RelapseCard(
    summary: ProgressSummary,
    onLogRelapse: () -> Unit
) {
    val today = summary.todayKey.ifBlank { DateUtils.todayKey() }
    val recentRelapse = summary.lastRelapse?.takeIf {
        DateUtils.daysBetween(it.dateKey, today) in 0..7
    }
    val isRecent = recentRelapse != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = MindSetSecondary.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MindSetSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isRecent) {
                        "Evaluasi Kambuh Terakhir"
                    } else {
                        "Mengalami Kekambuhan Hari Ini?"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))

            if (recentRelapse != null) {
                Text(
                    text = "Kambuh tercatat ${DateUtils.formatShortDate(recentRelapse.dateKey)} " +
                        "dengan pemicu \"${recentRelapse.triggerText}\".",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                if (recentRelapse.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Catatan: ${recentRelapse.note}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Rasio suksesmu masih ${formatPercent(summary.successRate)}. " +
                        "Satu kejadian tidak menghapus ${summary.totalCleanDays} hari kemenanganmu. " +
                        "Fokus pada pemicu, bukan rasa bersalah.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Text(
                    text = "Jangan hakimi dirimu sendiri. Pemulihan berbentuk spiral, bukan garis lurus. " +
                        "Catat pemicunya untuk memperkuat strategi pemulihan tanpa menghapus angka hari bersihmu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Rasio sukses masih ${formatPercent(summary.successRate)}! " +
                        "Satu kejadian tidak menghapus puluhan hari kemenanganmu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onLogRelapse,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary)
            ) {
                Text(
                    text = "Catat Evaluasi Kambuh (Bebas Stigma)",
                    color = MindSetNeutral,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PhasesCard(phases: List<CbtPhaseInfo>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pencapaian & Fase CBT",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                val activePhase = phases.indexOfFirst { !it.completed && !it.locked }
                if (activePhase >= 0) {
                    Surface(shape = RoundedCornerShape(10.dp), color = MindSetPrimaryContainer) {
                        Text(
                            text = "Fase ${activePhase + 1} Aktif",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            phases.forEachIndexed { index, phase ->
                PhaseItem(phase)
                if (index != phases.lastIndex) {
                    Box(
                        modifier = Modifier
                            .padding(start = 14.dp)
                            .width(2.dp)
                            .height(12.dp)
                            .background(MindSetDivider)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            val active = phases.firstOrNull { !it.completed && !it.locked }
            Text(
                text = when {
                    active != null ->
                        "Teruskan fase ini satu hari lagi untuk memperkuat jalur neuroplastisitas pemulihanmu."

                    phases.isNotEmpty() && phases.all { it.completed } ->
                        "Semua fase CBT telah selesai. Pertahankan gaya hidup barumu dengan bangga."

                    else ->
                        "Catat jurnal dan hindari kambuh untuk membuka fase berikutnya."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )
        }
    }
}

@Composable
private fun AffirmationCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetPrimaryContainer)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Filled.Spa,
                contentDescription = null,
                tint = MindSetPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = "Pemberitahuan Welas Asih",
                    style = MaterialTheme.typography.labelMedium,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral
                )
            }
        }
    }
}

@Composable
private fun RelapseDialog(
    uiState: ProgressUiState,
    onDismiss: () -> Unit,
    onTriggerChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Evaluasi Kambuh") },
        text = {
            Column {
                Text(
                    text = "Tidak ada hukuman di sini. Catat pemicunya agar strategi pemulihanmu makin kuat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = uiState.relapseTrigger,
                    onValueChange = onTriggerChange,
                    label = { Text("Pemicu utama") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.relapseNote,
                    onValueChange = onNoteChange,
                    label = { Text("Catatan (opsional)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                uiState.errorMessage?.let { error ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetError
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !uiState.isSaving) {
                Text(if (uiState.isSaving) "Menyimpan..." else "Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun StatCard(
    modifier: Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    sub: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = valueColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PhaseItem(phase: CbtPhaseInfo) {
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
                phase.completed -> Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                phase.locked -> Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MindSetNeutralGrey,
                    modifier = Modifier.size(14.dp)
                )

                else -> Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = phase.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (phase.locked) MindSetNeutralGrey else MindSetNeutral,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                if (phase.completed) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MindSetTertiaryLight) {
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetTertiary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (phase.locked) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MindSetNeutralLight.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "Terkunci",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = phase.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )
            if (!phase.completed && !phase.locked) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { phase.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MindSetPrimary,
                    trackColor = MindSetDivider
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${(phase.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

private fun formatPercent(rate: Float): String =
    String.format(Locale.US, "%.1f%%", rate.coerceIn(0f, 1f) * 100f)
