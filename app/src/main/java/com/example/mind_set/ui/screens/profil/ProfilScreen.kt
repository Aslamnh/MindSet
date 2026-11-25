package com.example.mind_set.ui.screens.profil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mind_set.ui.theme.*

@Composable
fun ProfilScreen() {
    var selectedTab   by remember { mutableStateOf(0) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var encryptionEnabled by remember { mutableStateOf(true) }
    val tabs = listOf("Semua", "Jurnal", "Kuis/Darurat", "Milestone")

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
                Text("Profil & Riwayat Pengguna", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = MindSetNeutralGrey)
        }

        // ── Profile header ────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MindSetPrimaryLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AP", fontSize = 22.sp, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Alex Pratama", style = MaterialTheme.typography.titleMedium, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = MindSetPrimaryContainer) {
                            Text("🏆 Pejuang Pulih • Fase 3", style = MaterialTheme.typography.labelSmall, color = MindSetPrimary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Bergabung sejak Jul 2026", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = MindSetNeutralGrey)
                    }
                }

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = MindSetDivider)
                Spacer(Modifier.height(12.dp))

                // Focus tag
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Psychology, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Fokus saat ini: Neuroplastisitas & Rekognisi Kognitif", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(4.dp))
                Text("Selangkah kecil menatap jalur impuls pikiran.", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)

                Spacer(Modifier.height(16.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProfileStat("42", "Hari Bersih\nKumulatif")
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(MindSetDivider))
                    ProfileStat("68", "Refleksi\nTotal")
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(MindSetDivider))
                    ProfileStat("12", "Kuis\nTuntatas")
                }

                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Progress Berkelanjutan", style = MaterialTheme.typography.labelSmall, color = MindSetTertiary, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Log tabs ──────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Riwayat & Log Pemulihan", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {}, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = MindSetNeutralGrey, modifier = Modifier.size(18.dp))
                    }
                }
                Text("Dokumentasi perjalanan pemulihan berbagai tipe", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                Spacer(Modifier.height(10.dp))

                // Tab row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tabs.forEachIndexed { i, tab ->
                        val isSelected = selectedTab == i
                        Surface(
                            modifier = Modifier
                                .clickable { selectedTab = i }
                                .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MindSetPrimary else Color.Transparent
                        ) {
                            Text(
                                text     = tab,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) Color.White else MindSetNeutralGrey,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Log entries
                val entries = listOf(
                    LogEntry("Kemarin, 21.15 WIB", "📝 Refleksi Malam", "Mengatasi Impuls Kelelahan",
                        "Mendiagnosis diri rasa lelah mental setelah jam kerja, dilakukan sebagai faktor pendorong. Menerapkan pengalihan mandiri ala hangat.",
                        "#PencegahanKambuh", "⭐⭐⭐⭐⭐", "Stres Kemenangan", false),
                    LogEntry("20 Okt 2026, 09.48 WIB", "🚨 Intervensi Krisis Kilat", "Box Breathing",
                        "Latihan pernapasan 4-4-4-4 selama 5 menit berhasil menurunkan kecemasan saat dorongan kompulsif muncul kuat.",
                        "Intensitas Dorongan Impuls • Level 4", "🔴 Sukses!", "Pemapasan Fase 3", false),
                    LogEntry("12 Okt 2026, 15.49 WIB", "🧘 Pemapasan Fase 3", "7 Hari Kesadaran Diri Terjaga",
                        "Mengidentifikasi 2 pola kognisi visual perasaan negatif berbasis stigma yang sering memicu dorongan.",
                        "#IdentitasDiri", "✨", "Milestone Pencapaian", true),
                    LogEntry("11 Okt 2026, 23.47 WIB", "📓 Catatan Malam Berbasis Stigma", "Menemukan Pemicu Larut Malam",
                        "Godaan memakai kebosanan dan kurang tidur sebagai alasan pembenar saya hanya berstrategi malam. Pola baru.",
                        "#IdentitasDiri • #ConsciousMindset", "🌙", "Refleksi Malam", false)
                )

                entries.forEach { entry ->
                    LogEntryItem(entry)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Privacy & Security ────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Privasi & Keamanan Data", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(14.dp))

                // Layer Sinkronisasi
                PrivacyToggleRow(
                    icon = "🛡️",
                    title = "Layer Sinkronisasi (Desentralisasi Kognitif)",
                    subtitle = "Penyimpanan aman & konten visual adaptif tersedia di Recent Apps & App-switcher di",
                    checked = encryptionEnabled,
                    onCheckedChange = { encryptionEnabled = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                // Kunci Biometrik
                PrivacyToggleRow(
                    icon = "🔐",
                    title = "Kunci Biometrik (Fingerprint / Face ID)",
                    subtitle = "Verifikasi instan tiap aplikasi dibuka setelah 1 menit tidak aktif.",
                    checked = biometricEnabled,
                    onCheckedChange = { biometricEnabled = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                // Ekspor
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {},
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📤", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ekspor Catatan Pemulihan", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                        Text("Format untuk arsip, termasuk untuk sesi konsultasi psikolog", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MindSetNeutralGrey)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                // Unduh Rekap
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {},
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📥", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Unduh Rekap Enkripsi (JSON & PDF Ringkas)", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                        Text("Penyimpanan Lokal 100% Aman (iOS 128-bit). Data tidak disimpan tanpa Anda!", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MindSetNeutralGrey)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MindSetDivider)

                // Edit Profil
                Row(
                    modifier = Modifier.fillMaxWidth().clickable {},
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("👤", fontSize = 18.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Edit Profil & Preferensi Afirmasi", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                        Text("Nama, pengingat preferensi, persona pendamping", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    }
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MindSetNeutralGrey)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Emergency counseling button ────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
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
                    Text("Pusat Bantuan & Konseling Darurat", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                    Text("Hubungi layanan konselor mitra berdedikasi", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = MindSetNeutral)
            }
        }

        Spacer(Modifier.height(10.dp))

        TextButton(
            onClick  = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔓 Keluar dari Perangkat ini", style = MaterialTheme.typography.labelMedium, color = MindSetNeutralGrey)
        }

        Spacer(Modifier.height(8.dp))
        Text(
            text     = "Mind-Set v2.4 • Ruangan Pemulihan Pribadimu",
            style    = MaterialTheme.typography.labelSmall,
            color    = MindSetNeutralGrey,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))
    }
}

data class LogEntry(
    val date: String,
    val type: String,
    val title: String,
    val body: String,
    val tags: String,
    val rating: String,
    val badge: String,
    val isMilestone: Boolean
)

@Composable
fun LogEntryItem(entry: LogEntry) {
    Row {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(
                    if (entry.isMilestone) MindSetSecondary
                    else MindSetTertiary
                )
                .padding(top = 4.dp)
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(entry.date, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                if (entry.isMilestone) {
                    Surface(shape = RoundedCornerShape(8.dp), color = MindSetSecondaryLight) {
                        Text("Tantangan Teratasi", style = MaterialTheme.typography.labelSmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(entry.type, style = MaterialTheme.typography.labelSmall, color = MindSetPrimary)
            Text(entry.title, style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(entry.body, style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey, maxLines = 3)
            Spacer(Modifier.height(4.dp))
            Text(entry.tags, style = MaterialTheme.typography.labelSmall, color = MindSetPrimary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MindSetNeutral, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun PrivacyToggleRow(icon: String, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor  = Color.White,
                checkedTrackColor  = MindSetPrimary
            )
        )
    }
}
