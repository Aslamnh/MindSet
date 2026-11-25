package com.example.mind_set.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.mind_set.ui.theme.*

@Composable
fun HomeScreen(
    currentUsername: String = "Alex",
    onEmergencyClick: () -> Unit
) {
    var selectedMood by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MindSetBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── 1. Top Bar ───────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MindSetPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("Mind-Set", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Text("Beranda", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = MindSetNeutral)
            }
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MindSetPrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentUsername.isNotBlank()) currentUsername.take(1).uppercase() else "A",
                    color = MindSetPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // ── 2. Greeting & Mood Check-in ──────────────────────────────────
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Halo, $currentUsername 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MindSetNeutral,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MindSetPrimaryContainer
                ) {
                    Text(
                        text = "⭐ Hari Ke-14",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Ruang tenangmu hari ini. Bagaimana perasaanmu sekarang?",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )

            Spacer(Modifier.height(14.dp))

            val moods = listOf("😌 Tenang", "😰 Cemas", "⚡ Semangat")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moods.forEach { mood ->
                    val selected = selectedMood == mood
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedMood = mood },
                        shape = RoundedCornerShape(14.dp),
                        color = if (selected) MindSetPrimaryContainer else MindSetSurface,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selected) MindSetPrimary else MindSetDivider
                        )
                    ) {
                        Text(
                            text = mood,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) MindSetPrimary else MindSetNeutral,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                        )
                    }
                }
            }
        }

        // ── 3. Emergency Button Card (Hero CTA) ──────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEmergencyClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = BorderStroke(1.5.dp, MindSetSecondary.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MindSetSecondary.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = MindSetNeutral,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Button",
                            style = MaterialTheme.typography.titleMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Intervensi CBT & Pernapasan Cepat",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Merasakan dorongan atau cemas berlebih? Aktifkan panduan pernapasan & evaluasi cepat sekarang.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutral,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onEmergencyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MindSetSecondary,
                        contentColor = MindSetNeutral
                    )
                ) {
                    Text(
                        text = "Aktifkan Mode Darurat",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ── 4. Clean Streak Card ─────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Clean Streak: 14 Hari Bersih",
                        style = MaterialTheme.typography.titleSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Milestone CBT Fase 2: Refleksi Pola",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Target: 21 Hari (Habit Baru)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                    Text(
                        text = "14/21 Hari (66%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.66f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MindSetTertiary,
                    trackColor = MindSetTertiaryLight
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MindSetPrimaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"Konsistensimu membentuk jalur saraf baru yang lebih sehat.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // ── 5. AI Recommendation Card ────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(20.dp), color = MindSetPrimaryContainer) {
                        Text(
                            text = "✨ Rekomendasi AI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFFE8F5E9)) {
                        Text(
                            text = "🔒 Privasi 100%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetTertiary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Pengalihan Fokus: Grounding 5-Menit",
                    style = MaterialTheme.typography.titleMedium,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Berdasarkan pola catatanmu: saat malam hari, luangkan waktu membaca buku fisik atau latihan pernapasan sebelum tidur.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MindSetPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌙", fontSize = 36.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Metode 5-4-3-2-1 • 5 Menit",
                            style = MaterialTheme.typography.labelMedium,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                        Spacer(Modifier.width(6.dp))
                        Text("Jalankan Latihan", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MindSetBackground)
                    ) {
                        Icon(Icons.Filled.BookmarkBorder, contentDescription = "Simpan", tint = MindSetNeutral)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Database Lokal Aman • Data tetap tersimpan di perangkatmu",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                }
            }
        }

        // ── 6. Journal Reminder Card ─────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📝", fontSize = 22.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Jurnal Refleksi Sore",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Penyelarasan Beban Pikiran",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Belum mengisi jurnal pemulihan hari ini? Catat 2 menit untuk melepaskan beban pikiran dan mengenali pemicunya.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MindSetNeutralGrey,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(14.dp))
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MindSetPrimary)
                ) {
                    Text(
                        text = "Tulis Jurnal Refleksi ✏️",
                        color = MindSetPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}
