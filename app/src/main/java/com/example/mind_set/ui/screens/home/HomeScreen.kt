package com.example.mind_set.ui.screens.home

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onEmergencyClick: () -> Unit) {
    var selectedMood by remember { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val progressRepo = remember { ProgressRepository.get(context) }
    val progressState by progressRepo.state.collectAsState()
    LaunchedEffect(Unit) { progressRepo.initialize() }

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
            // App logo mini
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MindSetPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Mind-Set", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Text("Beranda",  style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = MindSetNeutral)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MindSetPrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = MindSetPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // ── Greeting ─────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text  = "Halo, Alex 👋",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MindSetNeutral,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape  = RoundedCornerShape(20.dp),
                    color  = MindSetPrimaryContainer
                ) {
                    Text(
                        text     = "⭐ Fokus Hari Ke-14",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MindSetPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Ruang tenangmu hari ini. Bagaimana perasaanmu sekarang?",
                style = MaterialTheme.typography.bodyMedium,
                color = MindSetNeutralGrey
            )

            Spacer(Modifier.height(16.dp))

            // ── Mood chips ────────────────────────────────────
            val moods = listOf("😌 Tenang", "😰 Cemas", "⚡ Bersemangat")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                moods.forEach { mood ->
                    val selected = selectedMood == mood
                    Surface(
                        modifier = Modifier
                            .clickable { selectedMood = mood }
                            .border(
                                width = 1.dp,
                                color = if (selected) MindSetPrimary else MindSetDivider,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        shape = RoundedCornerShape(20.dp),
                        color = if (selected) MindSetPrimaryContainer else MindSetSurface
                    ) {
                        Text(
                            text     = mood,
                            style    = MaterialTheme.typography.labelMedium,
                            color    = if (selected) MindSetPrimary else MindSetNeutralGrey,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Emergency Button card ─────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { onEmergencyClick() },
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = MindSetSecondary, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = "Emergency Button",
                        style = MaterialTheme.typography.titleSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.weight(1f))
                    Text("⚠️", fontSize = 20.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Merasakan dorongan atau cemas berlebih? Tekan untuk panduan pemapasan & intervensi kilat.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick  = onEmergencyClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary)
                ) {
                    Text(
                        text  = "Mulai →",
                        color = MindSetNeutral,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Clean Streak card ─────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text  = "Clean Streak: ${progressState.currentStreak} Hari Bersih",
                        style = MaterialTheme.typography.titleSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "Pencapaian: Milestone CBT Fase 2 (Refleksi Pola)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetPrimary
                )
                Spacer(Modifier.height(12.dp))

                // Progress bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text  = "Menuju Target: 21 Hari (Habit baru)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                    Text(
                        text  = "${progressState.currentStreak}/21 Hari (${(progressState.currentStreak.coerceAtMost(21) / 21f * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (progressState.currentStreak.coerceAtMost(21) / 21f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color        = MindSetTertiary,
                    trackColor   = MindSetTertiaryLight
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape  = RoundedCornerShape(10.dp),
                    color  = MindSetPrimaryContainer
                ) {
                    Text(
                        text     = "\"${ProgressCalculator.appreciationMessage(progressState.currentStreak)}\"",
                        style    = MaterialTheme.typography.bodySmall,
                        color    = MindSetPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── AI Recommendation card ────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row {
                    Surface(shape = RoundedCornerShape(20.dp), color = MindSetPrimaryContainer) {
                        Text(
                            text     = "✨ Rekomendasi Personal AI (Lokal & Terenkripsi)",
                            style    = MaterialTheme.typography.labelSmall,
                            color    = MindSetPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFE8F5E9)) {
                    Text(
                        text     = "🔒 Privasi 100%",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MindSetTertiary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text  = "Pengalihan Fokus: Grounding 5-Menit",
                    style = MaterialTheme.typography.titleMedium,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text  = "Berdasarkan pola catatanmu: kemarin saat malam hari, luangkan waktu membaca buku fisik atau latihan pernapasan sebelum tidur.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(12.dp))

                // Placeholder image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MindSetPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌙", fontSize = 40.sp)
                        Text(
                            text  = "Metode 5-4-3-2-1 • 5 Menit",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick  = {},
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(Modifier.width(4.dp))
                        Text("Jalankan Latihan", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                    IconButton(
                        onClick  = {},
                        modifier = Modifier
                            .size(44.dp)
                            .border(1.dp, MindSetDivider, RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Filled.BookmarkBorder, contentDescription = "Simpan", tint = MindSetNeutralGrey)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = "Database Lokal Aman • Data tidak pernah meninggalkan perso mu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Journal reminder card ─────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text  = "Jurnal Refleksi Sore",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "Penyelarasan Beban Pikiran",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Belum mengisi jurnal pemulihan hari ini? Cata 2 menit untuk melepaskan beban pikiran dan mengenali pemicunya.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey
                )
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick  = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    border   = androidx.compose.foundation.BorderStroke(1.dp, MindSetPrimary)
                ) {
                    Text(
                        text       = "Tulis Jurnal Refleksi ✏️",
                        color      = MindSetPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
