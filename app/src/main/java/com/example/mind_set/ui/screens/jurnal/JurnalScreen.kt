package com.example.mind_set.ui.screens.jurnal

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
import com.example.mind_set.data.model.JournalItem
import com.example.mind_set.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JurnalScreen(
    viewModel: JournalViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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
                Text("Jurnal Pemulihan", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
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
                text  = "Enkripsi Lokal Aktif (Offline Ready Room DB)",
                style = MaterialTheme.typography.labelSmall,
                color = MindSetTertiary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.height(6.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text  = "Jurnal Harian & CBT AI",
                style = MaterialTheme.typography.headlineSmall,
                color = MindSetNeutral,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Tulis tanpa penghakiman. Dapatkan analisis CBT empatik seketika.",
                style = MaterialTheme.typography.bodySmall,
                color = MindSetNeutralGrey
            )
        }

        Spacer(Modifier.height(14.dp))

        // ── Tab selector (Form vs History & Calendar) ──────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.setTab(JournalTab.FORM) },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.currentTab == JournalTab.FORM) MindSetPrimary else MindSetSurface,
                    contentColor = if (uiState.currentTab == JournalTab.FORM) Color.White else MindSetNeutral
                )
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Tulis Jurnal", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.setTab(JournalTab.HISTORY) },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.currentTab == JournalTab.HISTORY) MindSetPrimary else MindSetSurface,
                    contentColor = if (uiState.currentTab == JournalTab.HISTORY) Color.White else MindSetNeutral
                )
            ) {
                Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Riwayat (${uiState.journals.size})", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Error / Success Banners
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetError.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Error, contentDescription = null, tint = MindSetError, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.errorMessage!!, style = MaterialTheme.typography.bodySmall, color = MindSetError)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.successMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetTertiaryLight)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MindSetTertiary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(uiState.successMessage!!, style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        // ── Tab Content ──────────────────────────────────────────────
        when (uiState.currentTab) {
            JournalTab.FORM -> {
                JournalFormContent(
                    uiState = uiState,
                    days = days,
                    dates = dates,
                    moods = moods,
                    tags = tags,
                    viewModel = viewModel
                )
            }
            JournalTab.HISTORY -> {
                JournalHistoryContent(
                    journals = uiState.journals,
                    onDelete = { viewModel.deleteJournal(it) },
                    onAddNew = { viewModel.setTab(JournalTab.FORM) }
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun JournalFormContent(
    uiState: JournalUiState,
    days: List<String>,
    dates: List<Int>,
    moods: List<String>,
    tags: List<String>,
    viewModel: JournalViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    Text("Pilih Tanggal Jurnal", style = MaterialTheme.typography.labelMedium, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                    Surface(shape = RoundedCornerShape(12.dp), color = MindSetTertiaryLight) {
                        Text("⭐ Clean Streak Aktif", style = MaterialTheme.typography.labelSmall, color = MindSetTertiary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    days.forEachIndexed { index, day ->
                        val isSelected = uiState.selectedDay == index
                        val isCompleted = index <= uiState.selectedDay
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { viewModel.setSelectedDay(index) }
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
                        Text("Identifikasi emosi & pikiran otomatis berbasis CBT", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
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
                        val isSelected = uiState.selectedMood == name
                        Surface(
                            modifier = Modifier
                                .clickable { viewModel.selectMood(name) }
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
                        val isSelected = uiState.selectedMood == name
                        Surface(
                            modifier = Modifier
                                .clickable { viewModel.selectMood(name) }
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
                    value = uiState.triggerText,
                    onValueChange = { viewModel.updateTriggerText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    placeholder = { Text("Contoh: Merasa lelah setelah jam kerja dan tergoda membuka media sosial...", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralLight) },
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
                    value = uiState.reframingText,
                    onValueChange = { viewModel.updateReframingText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    placeholder = { Text("Tulis sudut pandang alternatif yang berlandaskan fakta & welas asih...", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralLight) },
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
                        val isSelected = uiState.selectedTags.contains(tag)
                        Surface(
                            modifier = Modifier
                                .clickable { viewModel.toggleTag(tag) }
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
                        val isSelected = uiState.selectedTags.contains(tag)
                        Surface(
                            modifier = Modifier
                                .clickable { viewModel.toggleTag(tag) }
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
                    onClick  = { viewModel.saveJournal {} },
                    enabled  = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Simpan & Dapatkan Analisis CBT AI", color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                    }
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
    }
}

@Composable
fun JournalHistoryContent(
    journals: List<JournalItem>,
    onDelete: (Int) -> Unit,
    onAddNew: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Calendar Heatmap Mock / Summary Card ──────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MindSetSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("KALENDER JURNAL", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, letterSpacing = 1.sp, fontWeight = FontWeight.Bold)
                        Text("Oktober 2026", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.SemiBold)
                    }
                    Surface(shape = RoundedCornerShape(12.dp), color = MindSetPrimaryContainer) {
                        Text("${journals.size} Catatan Tersimpan", style = MaterialTheme.typography.labelSmall, color = MindSetPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text("Setiap titik hijau melambangkan refleksi harian yang sukses dicatat dan dianalisis.", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey)
            }
        }

        // ── List Header ───────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Riwayat Kronologis Jurnal", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
            TextButton(onClick = onAddNew) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Buat Baru", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }

        if (journals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MindSetSurface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📖", fontSize = 36.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Belum Ada Catatan Jurnal", style = MaterialTheme.typography.titleSmall, color = MindSetNeutral, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Mulai tulis jurnal pertamamu untuk melacak emosi & mendapatkan respons CBT AI.", style = MaterialTheme.typography.bodySmall, color = MindSetNeutralGrey, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onAddNew,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MindSetPrimary)
                    ) {
                        Text("Tulis Jurnal Sekarang", color = Color.White)
                    }
                }
            }
        } else {
            journals.forEach { item ->
                JournalItemCard(item = item, onDelete = { onDelete(item.id) })
            }
        }
    }
}

@Composable
fun JournalItemCard(item: JournalItem, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy • HH:mm", Locale.forLanguageTag("id-ID"))
    val dateString = dateFormat.format(Date(item.createdAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MindSetSurface)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(dateString, style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey)
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Mood: ${item.mood}", style = MaterialTheme.typography.labelMedium, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (item.status == "Tantangan Teratasi") MindSetSecondaryLight else MindSetPrimaryContainer
                    ) {
                        Text(
                            text = item.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = MindSetNeutral,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Hapus", tint = MindSetError, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MindSetDivider)
            Spacer(Modifier.height(10.dp))

            Text("PENGALAMAN / PEMICU:", style = MaterialTheme.typography.labelSmall, color = MindSetNeutralGrey, letterSpacing = 0.5.sp)
            Spacer(Modifier.height(2.dp))
            Text("\"${item.triggerText}\"", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral, fontWeight = FontWeight.Medium)

            if (item.reframingText.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text("REFRAMING CBT:", style = MaterialTheme.typography.labelSmall, color = MindSetSecondary, letterSpacing = 0.5.sp)
                Spacer(Modifier.height(2.dp))
                Text("\"${item.reframingText}\"", style = MaterialTheme.typography.bodySmall, color = MindSetNeutral)
            }

            if (item.tags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.tags.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MindSetBackground
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MindSetNeutralGrey,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (item.aiResponse.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MindSetPrimaryContainer.copy(alpha = 0.6f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.SmartToy, contentDescription = null, tint = MindSetPrimary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Analisis & Umpan Balik CBT AI", style = MaterialTheme.typography.labelMedium, color = MindSetPrimary, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(item.aiResponse, style = MaterialTheme.typography.bodySmall, color = MindSetNeutral)
                    }
                }
            }
        }
    }
}
