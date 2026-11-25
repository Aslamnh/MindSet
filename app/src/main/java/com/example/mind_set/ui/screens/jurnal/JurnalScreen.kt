package com.example.mind_set.ui.screens.jurnal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.mind_set.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JurnalScreen() {
    var selectedDay by remember { mutableStateOf(3) } // Kamis (index 3)
    var selectedMood by remember { mutableStateOf("Tenang") }
    var triggerText by remember { mutableStateOf("") }
    var reframingText by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(setOf<String>()) }

    val days   = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
    val dates  = listOf(20, 21, 22, 23, 24, 25, 26)
    val moods  = listOf("😌 Tenang", "😰 Cemas", "😤 Gelisah", "😐 Bosan", "😍 Tertarik")
    val tags   = listOf("#Kelelahan", "#Sendirian", "#StresKerja", "#MediaSosial", "#DukunganTeman")

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
                Text("Jurnal",   style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Filled.Notifications, contentDescription = null, tint = MindSetNeutralGrey)
        }

        // ── Offline badge ─────────────────────────────────────
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text  = "Enkripsi Lokal Aktif (Offline Ready)",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetTertiary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(6.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text  = "Jurnal Pemulihan Harian",
                style = MaterialTheme.typography.headlineSmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Tulis tanpa penghakiman. Semua data tersimpan aman dan privat di perangkatmu.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Week date picker ──────────────────────────────────
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
                    Text("Hari ini: Kamis, 24 Oktober 2026", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                    Surface(shape = RoundedCornerShape(12.dp), color = MindSetTertiaryLight) {
                        Text("⭐ 5 Hari Sadar Penuh", style = MaterialTheme.typography.labelSmall, color = MindSetTertiary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEachIndexed { index, day ->
                        val isSelected = selectedDay == index
                        val isCompleted = index < 3
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedDay = index }
                        ) {
                            Text(
                                text  = day,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) MindSetPrimary else MindSetNeutralGrey
                            )
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        when {
                                            isSelected  -> MindSetPrimary
                                            isCompleted -> MindSetTertiaryLight
                                            else        -> Color.Transparent
                                        }
                                    )
                                    .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted && !isSelected) {
                                    Text("✓", fontSize = 14.sp, color = MindSetTertiary, fontWeight = FontWeight.Bold)
                                } else {
                                    Text(
                                        text  = "${dates[index]}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (isSelected) Color.White else MindSetNeutral,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Refleksi Form ─────────────────────────────────────
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
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MindSetPrimary),
                        contentAlignment = Alignment.Center
                    ) { Text("1", color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Refleksi Kondisi & Pemicu Godaan", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                        Text("Langkah 1 Identifikasi emosi & pikiran otomatis", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text("Bagaimana perasaanmu saat ini?", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))

                // Mood chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.take(3).forEach { mood ->
                        val name = mood.substringAfter(" ")
                        val isSelected = selectedMood == name
                        Surface(
                            modifier = Modifier
                                .clickable { selectedMood = name }
                                .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MindSetPrimaryContainer else Color.Transparent
                        ) {
                            Text(
                                text     = mood,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) MindSetPrimary else MindSetNeutralGrey,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moods.drop(3).forEach { mood ->
                        val name = mood.substringAfter(" ")
                        val isSelected = selectedMood == name
                        Surface(
                            modifier = Modifier
                                .clickable { selectedMood = name }
                                .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MindSetPrimaryContainer else Color.Transparent
                        ) {
                            Text(
                                text     = mood,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) MindSetPrimary else MindSetNeutralGrey,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Text("Apa yang memicu dorongan atau pikiran negatif hari ini?", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = triggerText,
                    onValueChange = { triggerText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    placeholder = { Text("Contoh: Merasa lelah setelah jam kerja dan tergoda membuka media sosial/konten gelap...", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralLight) },
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = MindSetPrimary,
                        unfocusedBorderColor    = MindSetDivider,
                        focusedContainerColor   = MindSetSurface,
                        unfocusedContainerColor = MindSetSurface
                    )
                )

                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Bagaimana cara pandang yang lebih rasional?", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Surface(shape = RoundedCornerShape(10.dp), color = MindSetSecondaryLight) {
                        Text("Reframing CBT", style = MaterialTheme.typography.labelSmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = reframingText,
                    onValueChange = { reframingText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    placeholder = { Text("Klik sudut pandang 'alternatif' yang berlandaskan fakta dan kebaikan diri...", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralLight) },
                    shape  = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = MindSetPrimary,
                        unfocusedBorderColor    = MindSetDivider,
                        focusedContainerColor   = MindSetSurface,
                        unfocusedContainerColor = MindSetSurface
                    )
                )

                Spacer(Modifier.height(14.dp))
                Text("Pilih Tag Konteks Cepat:", style = MaterialTheme.typography.labelMedium, color = MindSetNeutralGrey)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.take(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                }
                                .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MindSetPrimaryContainer else Color.Transparent
                        ) {
                            Text(
                                text     = tag,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) MindSetPrimary else MindSetNeutralGrey,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.drop(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    selectedTags = if (isSelected) selectedTags - tag else selectedTags + tag
                                }
                                .border(1.dp, if (isSelected) MindSetPrimary else MindSetDivider, RoundedCornerShape(20.dp)),
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) MindSetPrimaryContainer else Color.Transparent
                        ) {
                            Text(
                                text     = tag,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = if (isSelected) MindSetPrimary else MindSetNeutralGrey,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick  = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Simpan & Dapatkan Respons CBT AI", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Daily affirmation ──────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSecondaryLight),
            border = androidx.compose.foundation.BorderStroke(1.dp, MindSetSecondary.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("KEBAIKAN DIRI HARI INI", style = MaterialTheme.typography.labelSmall, color = MindSetNeutral, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("\"Keinginan kambuh adalah gelombang alami. Kamu tidak perlu melawannya dengan keras hati; cukup perhatikan dan biarkan ia mereda.\"", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Recent entry ──────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape  = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Riwayat Refleksi Terakhir", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                    Text("Lihat Semua", style = MaterialTheme.typography.labelMedium, color = MindSetPrimary, fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MindSetDivider)
                Spacer(Modifier.height(10.dp))

                // Kemarin entry
                Row {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(MindSetTertiary).align(Alignment.Top).padding(top = 5.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kemarin, 21.15 WIB", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                            Surface(shape = RoundedCornerShape(10.dp), color = MindSetSecondaryLight) {
                                Text("Tantangan Teratasi", style = MaterialTheme.typography.labelSmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("CATATANMU:", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, letterSpacing = 0.5.sp)
                        Spacer(Modifier.height(2.dp))
                        Text("\"Hampir tergelincir buka aplikasi tarangtarang untuk bersungguh-sungguh. Memilih minum air putih dan mendengarkan musik santai.\"", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Respons CBT AI (Didukung Fallback Lokal)", style = MaterialTheme.typography.labelSmall, color = MindSetPrimary, fontWeight = FontWeight.Medium)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Keputusan bijak mengalihkan perhatian fisik! Mengganti respons impulsif ke kebiasaan netral adalah kemenangan kecil yang layak dirayakan. Keberhasilan menunda impuls 10 menit membukti...", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
