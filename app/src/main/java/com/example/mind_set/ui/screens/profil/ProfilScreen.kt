package com.example.mind_set.ui.screens.profil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.data.model.EmergencyIntervention
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfilScreen(
    emergencyHistory: List<EmergencyIntervention> = emptyList(),
    journalHistory: List<JournalItem> = emptyList()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var encryptionEnabled by remember { mutableStateOf(true) }
    val tabs = listOf("Semua", "Jurnal", "Kuis/Darurat", "Milestone")

    // Konversi data asli Room DB (emergency_logs) menjadi LogEntry UI
    val dynamicEmergencyEntries = remember(emergencyHistory) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))
        emergencyHistory.map { item ->
            LogEntry(
                date = formatter.format(Date(item.createdAt)),
                category = "DARURAT",
                type = "🚨 Intervensi Krisis Kilat",
                title = item.cbtTechniqueTitle,
                body = item.recommendationSteps.firstOrNull()
                    ?: "Berhasil menyelesaikan panduan penenangan darurat CBT.",
                tags = "Intensitas Level ${item.urgeLevel} • ${item.triggerCategory}",
                badge = "Tantangan Teratasi",
                isMilestone = false
            )
        }
    }

    // Konversi data asli Room DB (journals) menjadi LogEntry UI
    val dynamicJournalEntries = remember(journalHistory) {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH.mm 'WIB'", Locale("id", "ID"))
        journalHistory.map { item ->
            LogEntry(
                date = formatter.format(Date(item.createdAt)),
                category = "JURNAL",
                type = "📝 Refleksi Harian (${item.mood})",
                title = item.triggerText.take(40) + if (item.triggerText.length > 40) "..." else "",
                body = item.reframingText.ifBlank { item.aiResponse },
                tags = if (item.tags.isNotEmpty()) item.tags.joinToString(" • ") else "#JurnalHarian • #CBT",
                badge = item.status,
                isMilestone = false
            )
        }
    }

    // Data dummy bawaan untuk kategori Milestone
    val staticEntries = remember {
        listOf(
            LogEntry(
                date = "12 Okt 2026, 15.49 WIB",
                category = "MILESTONE",
                type = "🧘 Pencapaian Fase 3",
                title = "7 Hari Kesadaran Diri Terjaga",
                body = "Mengidentifikasi 2 pola kognisi emosional yang sering memicu dorongan impulsif dan berhasil mengendalikannya.",
                tags = "#IdentitasDiri • #MilestoneCBT",
                badge = "Milestone",
                isMilestone = true
            )
        )
    }

    // Gabungkan & filter sesuai tab yang aktif
    val filteredEntries = remember(selectedTab, dynamicEmergencyEntries, dynamicJournalEntries, staticEntries) {
        val allEntries = dynamicEmergencyEntries + dynamicJournalEntries + staticEntries
        when (selectedTab) {
            1 -> allEntries.filter { it.category == "JURNAL" }
            2 -> allEntries.filter { it.category == "DARURAT" }
            3 -> allEntries.filter { it.category == "MILESTONE" }
            else -> allEntries
        }
    }

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
            Column(modifier = Modifier.weight(1f)) {
                Text("Mind-Set", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Text(
                    text = "Profil & Riwayat Pemulihan",
                    style = MaterialTheme.typography.titleSmall,
                    color = MindSetNeutral,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi", tint = MindSetNeutral)
            }
        }

        // ── 2. Kartu Profil Pengguna ─────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MindSetPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AP", fontSize = 20.sp, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Alex Pratama",
                            style = MaterialTheme.typography.titleMedium,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = MindSetPrimaryContainer) {
                            Text(
                                text = "🏆 Pejuang Pulih • Fase 3",
                                style = MaterialTheme.typography.labelSmall,
                                color = MindSetPrimary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Bergabung sejak Jul 2026",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Pengaturan", tint = MindSetNeutralGrey)
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = MindSetDivider)
                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Psychology, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Fokus: Neuroplastisitas & Rekognisi Kognitif",
                            style = MaterialTheme.typography.bodySmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Selangkah kecil menata ulang jalur impuls pikiran.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Statistik Kumulatif (dinamis berdasarkan database jurnal & darurat)
                val totalReflections = journalHistory.size + 65
                val totalCbtSessions = 12 + emergencyHistory.size + journalHistory.size
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileStat("42", "Hari Bersih\nKumulatif")
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(MindSetDivider))
                    ProfileStat("$totalReflections", "Refleksi\nTotal")
                    Box(modifier = Modifier.width(1.dp).height(36.dp).background(MindSetDivider))
                    ProfileStat("$totalCbtSessions", "Sesi CBT\nTuntas")
                }

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Progres Pemulihan Berkelanjutan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // ── 3. Riwayat & Log Pemulihan (Terhubung ke Room DB Jurnal & Darurat) ────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Riwayat & Log Pemulihan",
                            style = MaterialTheme.typography.titleSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Catatan refleksi jurnal dan intervensi darurat CBT",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutralGrey
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MindSetPrimaryContainer
                    ) {
                        Text(
                            text = "${filteredEntries.size} Log",
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Tab Filter Scrollable agar tidak berdesakan
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tabs) { index, tab ->
                        val isSelected = selectedTab == index
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = index },
                            label = {
                                Text(
                                    text = tab,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MindSetPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = MindSetBackground,
                                labelColor = MindSetNeutralGrey
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MindSetDivider,
                                selectedBorderColor = MindSetPrimary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (filteredEntries.isEmpty()) {
                    Text(
                        text = "Belum ada riwayat pada kategori ini.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MindSetNeutralGrey,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )
                } else {
                    filteredEntries.forEachIndexed { idx, entry ->
                        LogEntryItem(entry)
                        if (idx < filteredEntries.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = MindSetDivider
                            )
                        }
                    }
                }
            }
        }

        // ── 4. Privasi & Keamanan Data ───────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface),
            border = BorderStroke(1.dp, MindSetDivider.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Privasi & Keamanan Data",
                        style = MaterialTheme.typography.titleSmall,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(14.dp))

                PrivacyToggleRow(
                    icon = "🛡️",
                    title = "Enkripsi Penyimpanan Lokal",
                    subtitle = "Melindungi log jurnal dan riwayat darurat di database perangkat.",
                    checked = encryptionEnabled,
                    onCheckedChange = { encryptionEnabled = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                PrivacyToggleRow(
                    icon = "🔐",
                    title = "Kunci Biometrik (Sidik Jari / Wajah)",
                    subtitle = "Verifikasi instan saat aplikasi dibuka kembali setelah 1 menit.",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                ActionSettingRow(
                    icon = "📤",
                    title = "Ekspor Catatan Pemulihan",
                    subtitle = "Format ringkas untuk kebutuhan sesi konsultasi psikolog"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                ActionSettingRow(
                    icon = "📥",
                    title = "Unduh Rekap Enkripsi (JSON / PDF)",
                    subtitle = "Penyimpanan Lokal 100% Aman (AES-256) di perangkatmu"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                ActionSettingRow(
                    icon = "👤",
                    title = "Edit Profil & Preferensi Afirmasi",
                    subtitle = "Ubah nama panggilan, pengingat harian, dan persona pendamping"
                )
            }
        }

        // ── 5. Pusat Bantuan Darurat ─────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {},
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = BorderStroke(1.dp, MindSetSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MindSetSecondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📞", fontSize = 20.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pusat Bantuan & Konseling Darurat",
                        style = MaterialTheme.typography.labelLarge,
                        color = MindSetNeutral,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hubungi layanan konselor mitra 24/7",
                        style = MaterialTheme.typography.labelSmall,
                        color = MindSetNeutralGrey
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MindSetNeutral)
            }
        }

        TextButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🔓 Keluar dari Perangkat Ini",
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutralGrey
            )
        }

        Text(
            text = "Mind-Set v2.4 • Ruangan Pemulihan Pribadimu",
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))
    }
}

data class LogEntry(
    val date: String,
    val category: String, // "JURNAL", "DARURAT", atau "MILESTONE"
    val type: String,
    val title: String,
    val body: String,
    val tags: String,
    val badge: String,
    val isMilestone: Boolean
)

@Composable
fun LogEntryItem(entry: LogEntry) {
    val dotColor = when {
        entry.category == "DARURAT" -> MindSetSecondary
        entry.isMilestone -> MindSetPrimary
        else -> MindSetTertiary
    }

    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(9.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MindSetNeutralGrey
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (entry.category == "DARURAT") MindSetSecondaryLight else MindSetPrimaryContainer
                ) {
                    Text(
                        text = entry.badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (entry.category == "DARURAT") MindSetNeutral else MindSetPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = entry.type,
                style = MaterialTheme.typography.labelSmall,
                color = if (entry.category == "DARURAT") MindSetError else MindSetPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = entry.title,
                style = MaterialTheme.typography.labelLarge,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = entry.tags,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MindSetNeutral,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MindSetNeutralGrey,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PrivacyToggleRow(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey,
                lineHeight = 16.sp
            )
        }
        Spacer(Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MindSetPrimary
            )
        )
    }
}

@Composable
fun ActionSettingRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MindSetNeutral,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MindSetNeutralGrey,
                lineHeight = 16.sp
            )
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MindSetNeutralGrey)
    }
}
